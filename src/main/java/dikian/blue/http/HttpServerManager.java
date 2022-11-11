package dikian.blue.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dikian.blue.Nov_RPG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpServerManager {
    private final String DEFAULT_HOSTNAME = "0.0.0.0";
    private final int DEFAULT_PORT = 8080;
    private final int DEFAULT_BACKLOG = 0;
    private static HttpServer server = null;

    public HttpServerManager() throws IOException {
        createServer(DEFAULT_HOSTNAME, DEFAULT_PORT);
    }
    public HttpServerManager(int port) throws IOException {
        createServer(DEFAULT_HOSTNAME, port);
    }
    public HttpServerManager(String host, int port) throws IOException {
        createServer(host, port);
    }


    private void createServer(String host, int port) throws IOException {
        // HTTP Server 생성
        this.server = HttpServer.create(new InetSocketAddress(host, port), DEFAULT_BACKLOG);
        // HTTP Server Context 설정
        server.createContext("/", new RootHandler());
    }


    public void start() {
        server.start();
    }

    public static void stop(int delay) {
        server.stop(delay);
    }

    public static void main() {
        HttpServerManager httpServerManager = null;

        try {
            // 시작 로그
            System.out.println(
                    String.format(
                            "[%s][HTTP SERVER][START]",
                            new SimpleDateFormat("yyyy-MM-dd H:mm:ss").format(new Date())
                    )
            );

            // 서버 생성
            httpServerManager = new HttpServerManager("localhost", 1000);
            httpServerManager.start();
            // Shutdown Hook
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    // 종료 로그
                    System.out.println(
                            String.format(
                                    "[%s][HTTP SERVER][STOP]",
                                    new SimpleDateFormat("yyyy-MM-dd H:mm:ss").format(new Date())
                            )
                    );
                }
            }));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    class RootHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            // Initialize Response Body
            OutputStream respBody = exchange.getResponseBody();

            try {
                int online = 0;
                for (Player players: Bukkit.getServer().getOnlinePlayers()) {
                    online++;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("<!DOCTYPE html><html lang='ko' dir='ltr'><head><title>Nov RPG web source</title>");
                sb.append("<link href='https://nov-rpg-web-source.netlify.app/css/style.css', rel='stylesheet'>");
                sb.append("<link href='https://nov-rpg-web-source.netlify.app/css/animation.css', rel='stylesheet'></head><body>");
                sb.append("<script src='https://nov-rpg-web-source.netlify.app/js/style.js'></script><nav id='nav'>");
                sb.append("<img src='https://nov-rpg-web-source.netlify.app/img/Logo.png'><ul class='main-nav'>");
                sb.append("<li><a>홈</a></li><li><a>관리자</a></li><li><a>서버</a></li><li><a>후원</a></li></ul></nav><div id='space'></div>");
                sb.append("<div class='welcome'><h1>밤마 서버에 오신것을 환영합니다!</h1><h2>현재 접속중인 인원: " + online + "</h2></div>");
                sb.append("<div class='description'><img src='https://nov-rpg-web-source.netlify.app/img/play_1.png'>");
                sb.append("<h1>밤마 서버</h1><h2>여러 직업과 다양한 스토리를 즐기실 수 있습니다. 또 매 달 특별한 이벤트를 진행한답니다! 놓치지 마세요!</h2>");
                sb.append("<a href=''>자세히 알아보기 →</a></div>");
                sb.append("<div class='boost'><h1>후원</h1><h2>서버 성장에 도움을 주세요. 여러가지 후원 혜택이 주어진답니다.</h2>");
                sb.append("<a href=''>자세히 알아보기 →</a>");
                sb.append("<img src='https://nov-rpg-web-source.netlify.app/img/play_2.png'></div>");
                sb.append("<footer>Copyrightⓒ DikianBlue All Right Reserved.</footer></body></html>");



                // Encoding to UTF-8
                ByteBuffer bb = Charset.forName("UTF-8").encode(sb.toString());
                int contentLength = bb.limit();
                byte[] content = new byte[contentLength];
                bb.get(content, 0, contentLength);

                // Set Response Headers
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "text/html;charset=UTF-8");
                headers.add("Content-Length", String.valueOf(contentLength));

                // Send Response Headers
                exchange.sendResponseHeaders(200, contentLength);

                respBody.write(content);

                // Close Stream
                // 반드시, Response Header를 보낸 후에 닫아야함
                respBody.close();

            } catch ( IOException e ) {
                e.printStackTrace();

                if( respBody != null ) {
                    respBody.close();
                }
            } finally {
                exchange.close();
            }
        }
    }
}
