package dikian.blue.systems;

import com.mojang.authlib.GameProfile;
import dikian.blue.Nov_RPG;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NPC implements CommandExecutor, Listener {
    public void EntityPlayer() {}

    private static File file;
    private static FileConfiguration customFile;

    Map<String, EntityPlayer> npc = new HashMap<>();

    public static void setup() {
        file = new File(Bukkit.getPluginManager().getPlugin("Nov_RPG").getDataFolder(), "npc.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                //
            }
        }
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return customFile;
    }

    public static void save() {
        try {
            customFile.save(file);
        } catch (IOException e) {
            System.out.println("§cCouldn't save Config File.");
        }
    }

    public static void reload() {
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    @EventHandler
    public void onEnable(PluginEnableEvent e) {
        for (String keys: NPC.get().getKeys(false)) {
            String[] data = NPC.get().getString(keys).replace("[", "").replace("]", "").split(", ");
            MinecraftServer server = ((CraftServer)Bukkit.getServer()).getServer();
            WorldServer world = ((CraftWorld)Bukkit.getServer().getWorld(data[0])).getHandle();

            EntityPlayer value = new EntityPlayer(server, ((CraftWorld)Bukkit.getServer().getWorld(data[0])).getHandle(), new GameProfile(UUID.randomUUID(),
                    Base.chatColor(data[4])), new PlayerInteractManager(((CraftWorld)Bukkit.getServer().getWorlds().get(0)).getHandle()));
            value.setLocation(Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3]), 0, 0);
            value.setNoGravity(false);
            npc.put(keys, value);
        }

        for (Player players : Bukkit.getOnlinePlayers()) {
            npc.forEach((key, value) -> {
                PlayerConnection connection = ((CraftPlayer) players).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, value));
                connection.sendPacket(new PacketPlayOutNamedEntitySpawn(value));
            });
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    npc.forEach((key, value) -> {
                        PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, value));
                    });
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("Nov_RPG"), 5, 1);
    }

    @EventHandler
    public void onDisable(PluginDisableEvent e) {
        npc.forEach((key, value) -> {
            for (Player players : Bukkit.getOnlinePlayers()) {
                PlayerConnection connection = ((CraftPlayer) players).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, value));
                connection.sendPacket(new PacketPlayOutEntityDestroy(value.getId()));
            }
        });
    }

    @EventHandler
    public void onKnightInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("");
    }


    @EventHandler
    public void onInteraction(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        p.sendMessage(e.getRightClicked().getName());
        p.sendMessage("");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equals("npc")) {
            if (sender.isOp()) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (args.length < 4) {
                        p.sendMessage(Nov_RPG.preffix + "/npc <x> <y> <z> <이름>");
                    } else {
                        List<String> arg_loc = new ArrayList<String>();
                        arg_loc.add(p.getWorld().getName());
                        String name = "";
                        for (int i = 0; i < args.length; i++) {
                            if (args[i].equals("~")) {
                                if (i == 0) {
                                    arg_loc.add(p.getLocation().getX() + "");
                                }
                                if (i == 1) {
                                    arg_loc.add(p.getLocation().getY() + "");
                                }
                                if (i == 2) {
                                    arg_loc.add(p.getLocation().getZ() + "");
                                }
                            } else {
                                if (i <= 2) {
                                    arg_loc.add(args[i]);
                                } else {
                                    if (i == 3) {
                                        name += args[3];
                                    } else {
                                        name += " " + args[i];
                                    }
                                }
                            }
                        }
                        arg_loc.add(name);
                        p.sendMessage(Base.chatColor(arg_loc + ""));
                        int key_num = 0;
                        for (String str : NPC.get().getKeys(false)) {
                            key_num += 1;
                        }

                        int keys = 0;

                        for (int i = 1; i <= key_num + 1; i++) {
                            if (NPC.get().get(i + "") == null) {
                                NPC.get().set(i + "", arg_loc + "");
                                keys = i;
                                break;
                            }
                        }
                        NPC.save();

                        MinecraftServer server = ((CraftServer)Bukkit.getServer()).getServer();

                        EntityPlayer value = new EntityPlayer(server, ((CraftWorld)Bukkit.getServer().getWorld(arg_loc.get(0))).getHandle(), new GameProfile(UUID.randomUUID(),
                                Base.chatColor(arg_loc.get(4))), new PlayerInteractManager(((CraftWorld)Bukkit.getServer().getWorlds().get(0)).getHandle()));
                        value.setLocation(Double.parseDouble(arg_loc.get(1)), Double.parseDouble(arg_loc.get(2)), Double.parseDouble(arg_loc.get(3)), 0, 0);
                        npc.put(String.valueOf(keys), value);
                        p.sendMessage(Nov_RPG.preffix + "NPC(ID " + keys + ")가 새로 생성되었습니다.");
                    }
                }
            }
        }
        return false;
    }
}
