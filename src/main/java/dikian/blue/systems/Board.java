package dikian.blue.systems;

import dikian.blue.files.CurrencyFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Board implements Listener {
    Map<Player, Boolean> board_check = new HashMap<>();

    public void board(Player p) {
        CurrencyFile.uuid = p.getUniqueId() + "";
        CurrencyFile.setup();
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("§e§l﴾ Info ﴿", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        DecimalFormat decFormat = new DecimalFormat("###,###");

        Score money = obj.getScore("§e소유 니트: §6" + decFormat.format(CurrencyFile.get().getInt("Money")) + "니트");
        money.setScore(99);
        Score cash = obj.getScore("§e캐시: §6" + decFormat.format(CurrencyFile.get().getInt("Cash")) + "캐시");
        cash.setScore(98);
        Score events = obj.getScore("§e☆현재 진행중인 이벤트가 없습니다☆");
        events.setScore(-99);
        p.setScoreboard(board);
    }

    @EventHandler
    public void start(PluginEnableEvent e) {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (board_check.get(pl) == null) {
                board_check.put(pl, true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (pl.isOnline()) {
                            board(pl);
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Bukkit.getPluginManager().getPlugin("Nov_RPG"), 5, 1);
            }
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (p.isOnline()) {
                    board(p);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("Nov_RPG"), 5, 1);
    }
}
