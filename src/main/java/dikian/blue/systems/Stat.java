package dikian.blue.systems;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import dikian.blue.files.Config;
import dikian.blue.files.StatUserFile;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Stat implements Listener, CommandExecutor {

    // Config Info
    // 힘, 체력, 크리티컬 확률, 치명타 확률, 치명타 공격력, 방어력, 회피율

    /**
    힘: 레벨당 0.7 공격 상승
    체력: 레벨당 5.5 체력 상승, 0.1 공격 상승
    CC(치명확률): 레벨당 0.3% 치명확률 증가
    CD(치명타딜): 레벨당 0.4% 치명딜 증가
    방어: 레벨당 0.3 방어력 증가
    회피: 레벨당 0.3% 회피량 증가**/


    // Valuable
    public static Map<UUID, Double> health = new HashMap<>();
    public static Map<Player, Double> mana = new HashMap<>();

    public void actionbar(Player p) {
        StatUserFile.uuid = p.getUniqueId() + "";
        StatUserFile.setup();
        String[] data = StatUserFile.get().getString("Stat").split(", ");
        health.putIfAbsent(p.getUniqueId(), 20 + Integer.parseInt(data[1]) * 5.5);
        double printHealth = health.get(p.getUniqueId());
        if (health.get(p.getUniqueId()) / ((20 + Integer.parseInt(data[1]) * 5.5) / 20) > 20) {
            health.put(p.getUniqueId(), 20 + Integer.parseInt(data[1]) * 5.5);
        }
        if (health.get(p.getUniqueId()) / ((20 + Integer.parseInt(data[1]) * 5.5) / 20) > 0) {
            p.setHealth(health.get(p.getUniqueId()) / ((20 + Integer.parseInt(data[1]) * 5.5) / 20));
        } else {
            health.put(p.getUniqueId(), 20 + Integer.parseInt(data[1]) * 5.5);
            String[] loc = Config.get().getString("Spawn Location").split(",");
            p.teleport(new Location(Bukkit.getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3])));
            p.sendMessage("You Died!");
        }
        mana.putIfAbsent(p, 100.0);
        double printMana = mana.get(p);
        if (mana.get(p) > 100) {
            mana.put(p, 100.0);
        }
        if (!(mana.get(p) >= 0)) {
            mana.put(p, 0.0);
        }
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR.ACTION_BAR, TextComponent.fromLegacyText("§c❤ " +
                (int) printHealth + "/" + (int) ((Integer.parseInt(data[1]) * 5.5) + 20) + "          §9✦ " + (int) printMana + "/" + (int) (100.0)));
    }

    public void inventory(Player p) {
        StatUserFile.uuid = p.getUniqueId() + "";
        StatUserFile.setup();
        String[] data = StatUserFile.get().getString("Stat").split(",");
        Inventory inv = Bukkit.createInventory(null, 9 * 6, "§9스텟");
        ItemStack item = new ItemStack(Material.GOLD_SPADE);
        int plus_slot = 10;
        for (int i = 0; i <= 5; i++) {
            item.setDurability((short) ((short) i + 1));
            ItemMeta meta = item.getItemMeta();
            meta.setUnbreakable(true);
            List<String> lore = new ArrayList<String>();
            if (i == 0) {
                meta.setDisplayName("§c근력 §6[" + data[0] + "§6]");
                lore.add("§8§o- 공격력 §c§o" + Math.round((1.7 * Integer.parseInt(data[0])*10))/10 + " §8§o증가");
            } if (i == 1) {
                meta.setDisplayName("§a체력 §6[" + data[1] + "§6]");
                lore.add("§8§o- 체력 §a§o" + Math.round((5.5 * Integer.parseInt(data[1])*10))/10 + " §8§o증가");
                lore.add("§8§o- 공격력 §c§o" + Math.round((0.1 * Integer.parseInt(data[1])*10))/10 + " §8§o증가");
            } if (i == 2) {
                meta.setDisplayName("§9치명타 확률 §6[" + data[2] + "§6]");
                lore.add("§8§o- 치명타 확률 §9§o" + Math.round((0.3 * Integer.parseInt(data[2])*10))/10 + " §8§o증가");
            } if (i == 3) {
                meta.setDisplayName("§b치명타 공격력 §6[" + data[3] + "§6]");
                lore.add("§8§o- 치명타 공격력 §b§o" + Math.round((0.4 * Integer.parseInt(data[3])*10))/10 + " §8§o증가");
            } if (i == 4) {
                meta.setDisplayName("§7방어력 §6[" + data[4] + "§6]");
                lore.add("§8§o- 방어력 §7§o" + Math.round((0.3 * Integer.parseInt(data[4])*10))/10 + " §8§o증가");
            } if (i == 5) {
                meta.setDisplayName("§f회피율 §6[" + data[5] + "§6]");
                lore.add("§8§o- 회피율 §f§o" + Math.round((0.1 * Integer.parseInt(data[5])*10))/10 + " §8§o증가");
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(plus_slot, item);
            if (i == 2) {
                plus_slot += 21;
            } else {
                plus_slot += 3;
            }
            p.openInventory(inv);
            if (StatUserFile.get().getInt("StatPoint") == 0) {
                p.sendTitle("§c스텟포인트               스텟포인트", "§c0" +
                        "                                                "
                        + "§c0", 0, 20 * 60 * 60, 0);
            } else {
                p.sendTitle("§6스텟포인트               스텟포인트", "§6" + StatUserFile.get().getString("StatPoint") +
                        "                                                "
                        + "§6" + StatUserFile.get().getString("StatPoint") + "", 0, 20 * 60 * 60, 0);
            }
        }
    }

    public void stat(Player p, Integer Int) {
        StatUserFile.uuid = p.getUniqueId() + "";
        StatUserFile.setup();
        Integer sp = StatUserFile.get().getInt("StatPoint");
        String[] data = StatUserFile.get().getString("Stat").split(", ");
        if (Integer.parseInt(data[Int]) < 300) {
            if (sp != 0) {
                String str = null;
                StatUserFile.get().set("StatPoint", (StatUserFile.get().getInt("StatPoint") - 1));
                for (int i = 0; i <= 5; i++) {
                    if (i == 5) {
                        if (i == Int) {
                            str += (Integer.parseInt(data[i]) + 1) + "";
                        } else {
                            str += data[i] + "";
                        }
                    } else {
                        if (i == 0) {
                            if (i == Int) {
                                str = (Integer.parseInt(data[i]) + 1) + ", ";
                            } else {
                                str = data[i] + ", ";
                            }
                        } else {
                            if (i == Int) {
                                str += "" + (Integer.parseInt(data[i]) + 1) + ", ";
                            } else {
                                str += "" + data[i] + ", ";
                            }
                        }
                    }
                    StatUserFile.get().set("Stat", str);
                }
            }
            StatUserFile.save();
            StatUserFile.reload();
        }
    }


    // Events
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        StatUserFile.uuid = p.getUniqueId() + "";
        StatUserFile.setup();
        StatUserFile.get().options().copyDefaults(true);
        if (StatUserFile.get().getString("Stat") == null && StatUserFile.get().getString("StatPoint") == null) {
            StatUserFile.get().set("Stat", "0, 0, 0, 0, 0, 0");
            StatUserFile.get().set("StatPoint", 0);
        }
        StatUserFile.save();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (p.isOnline()) {
                    actionbar(p);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("Nov_RPG"), 5, 1);
    }

    @EventHandler
    public void onEnable(PluginEnableEvent e) {
        for (Player p: Bukkit.getOnlinePlayers()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (p.isOnline()) {
                        actionbar(p);
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("Nov_RPG"), 5, 1);
        }
    }

    @EventHandler
    public void onDamaged(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            StatUserFile.uuid = p.getUniqueId() + "";
            String[] data = StatUserFile.get().getString("Stat").split(", ");
            if (new Random().nextInt(100) < Integer.parseInt(data[5]) * 0.1) {
                p.sendTitle("", "                         §e§o회피!", 0, 40, 0);
                e.setDamage(0);
            } else {
               if ( e.getDamage() - Integer.parseInt(data[4]) * 0.3 < 0) {
                   e.setDamage(0);
               } else {
                   e.setDamage(e.getDamage() - Integer.parseInt(data[4]) * 0.3);
               }
                health.put(p.getUniqueId(), health.get(p.getUniqueId()) - e.getDamage());
                e.setDamage(0);
            }
        }
    }

    @EventHandler
    public void onLevelUp(PlayerLevelChangeEvent e) {
        if (e.getOldLevel() < e.getNewLevel()) {
            StatUserFile.uuid = e.getPlayer().getUniqueId() + "";
            StatUserFile.setup();
            StatUserFile.get().options().copyDefaults(true);
            StatUserFile.get().set("StatPoint", StatUserFile.get().getInt("StatPoint") + (e.getNewLevel() - e.getOldLevel()) * 2);
            StatUserFile.save();
        }
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        p.sendTitle("", "", 0, 0, 0);
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            if (e.getClickedInventory() != null) {
                if (e.getClickedInventory().getName().equalsIgnoreCase("§9스텟")) {
                    Player p = (Player) e.getWhoClicked();

                    e.setCancelled(true);
                    if (e.getSlot() == 10) {
                        stat(p, 0);
                        inventory(p);
                    }
                    if (e.getSlot() == 13) {
                        stat(p, 1);
                        inventory(p);
                    }
                    if (e.getSlot() == 16) {
                        stat(p, 2);
                        inventory(p);
                    }
                    if (e.getSlot() == 37) {
                        stat(p, 3);
                        inventory(p);
                    }
                    if (e.getSlot() == 40) {
                        stat(p, 4);
                        inventory(p);
                    }
                    if (e.getSlot() == 43) {
                        stat(p, 5);
                        inventory(p);
                    }
                }
            }
        }
    }


    // Commands
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("스텟")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 0) {
                    inventory(p);
                }
            }
        }
        return false;
    }
}
