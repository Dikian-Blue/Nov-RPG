package dikian.blue.systems;

import dikian.blue.Nov_RPG;
import dikian.blue.files.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class Emoji implements CommandExecutor, Listener {
    public Map<Player, Integer> timer = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("e")) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;

                    int MaxEmoji = Config.get().getInt("Max-Imoji");
                    Inventory inv = null;
                    try {
                        inv = Bukkit.createInventory(null, Integer.parseInt(MaxEmoji + ""), "§9이모지");
                    } catch (Exception e) {
                        inv = Bukkit.createInventory(null, ((int) (MaxEmoji/9) + 1) * 9, "§9이모지");
                    }
                    for (int i = 0; i < MaxEmoji; i++) {
                        ItemStack item = new ItemStack(Material.GOLD_HOE);
                        ItemMeta meta = item.getItemMeta();
                        meta.setUnbreakable(true);
                        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                        meta.setDisplayName("§6" + (i + 1)  + ".");
                        item.setItemMeta(meta);
                        item.setDurability((short) (i + 1));
                        inv.setItem(i, item);
                    }
                    p.openInventory(inv);
                }
            }
            else if (args[0].equals("reload")) {
                if (sender.isOp()) {
                    Config.reload();
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getClickedInventory() != null) {
            if (e.getInventory().getName().contains("§9이모지")) {
                int ClickedSlot = e.getSlot() + 1;
                if (ClickedSlot <= Config.get().getInt("Max-Imoji")) {
                    p.closeInventory();
                    ArmorStand stand = (ArmorStand) p.getWorld().spawnEntity(new Location(p.getWorld(), p.getLocation().getX()
                            , p.getLocation().getY() + 100, p.getLocation().getX()), EntityType.ARMOR_STAND);
                    stand.setSmall(true);
                    stand.setVisible(false);
                    stand.setGravity(false);
                    stand.setMarker(true);
                    ItemStack item = new ItemStack(Material.GOLD_HOE);
                    item.setDurability((short) ClickedSlot);
                    stand.setHelmet(item);
                    stand.teleport(p.getLocation().add(0, 1, 0));
                    timer.put(p, 0);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (timer.get(p) >= 40) {
                                stand.remove();
                                this.cancel();
                            } else {
                                stand.teleport(p.getLocation().add(0, 1, 0));
                                timer.put(p, timer.get(p) + 1);
                            }
                        }
                    }.runTaskTimer(Bukkit.getPluginManager().getPlugin("Nov_RPG"), 5, 1);
                }
            }
        }
    }
}
