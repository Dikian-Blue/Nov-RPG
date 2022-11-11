package dikian.blue.systems;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.management.Attribute;

public class Equipment implements Listener, CommandExecutor {
    // Valuables
    public void equipment_window(Player p, Player watch) {
        Inventory inv = Bukkit.createInventory(null, 9 * 6, "§9장비창 - " + p.getName());
        ItemStack space = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0);
        ItemMeta spacemeta = space.getItemMeta();
        spacemeta.setDisplayName(" ");
        space.setItemMeta(spacemeta);
        for (int i = 0; i <= 53; i++) {
            inv.setItem(i, space);
        }
        ItemStack item = new ItemStack(Material.GOLD_SPADE);
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.setDisplayName("§6투구");
        item.setItemMeta(meta);
        item.setDurability((short) 7);
        inv.setItem(10, item);
        meta.setDisplayName("§6흉갑");
        item.setItemMeta(meta);
        item.setDurability((short) 8);
        inv.setItem(19, item);
        meta.setDisplayName("§6각반");
        item.setItemMeta(meta);
        item.setDurability((short) 9);
        inv.setItem(28, item);
        item.setDurability((short) 10);
        meta.setDisplayName("§6신발");
        item.setItemMeta(meta);
        inv.setItem(37, item);
        inv.setItem(11, p.getInventory().getHelmet());
        inv.setItem(20, p.getInventory().getChestplate());
        inv.setItem(29, p.getInventory().getLeggings());
        inv.setItem(38, p.getInventory().getBoots());
        ItemStack weapon = new ItemStack(Material.GOLD_SPADE);
        ItemMeta weaponMeta = weapon.getItemMeta();
        weaponMeta.setUnbreakable(true);
        weaponMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        weaponMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        for (int i = 22; i <= 25; i++) {
            if (i==22) {
                weaponMeta.setDisplayName("§6주무기");
                weapon.setItemMeta(weaponMeta);
            } if (i==23) {
                weaponMeta.setDisplayName("§6보조무기");
                weapon.setItemMeta(weaponMeta);
            } if (i==24) {
                weaponMeta.setDisplayName("§6<미정>");
                weapon.setItemMeta(weaponMeta);
            } if (i==25) {
                weaponMeta.setDisplayName("§6<미정>");
                weapon.setItemMeta(weaponMeta);
            }
            weapon.setDurability((short) (i - 11));
            inv.setItem(i, weapon);
        }
        for (int i = 31; i <= 34; i++) {
            inv.setItem(i, new ItemStack(Material.AIR));
        }
        watch.openInventory(inv);
    }



    // Events
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase("§9장비창 - " + e.getPlayer().getName())) {
            e.getPlayer().getInventory().setHelmet(e.getInventory().getItem(11));
            e.getPlayer().getInventory().setChestplate(e.getInventory().getItem(20));
            e.getPlayer().getInventory().setLeggings(e.getInventory().getItem(29));
            e.getPlayer().getInventory().setBoots(e.getInventory().getItem(38));
        }
    }

    @EventHandler
    public void onClickByEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (e.getPlayer().isSneaking()) {
            if (e.getRightClicked() instanceof Player) {
                equipment_window((Player) e.getRightClicked(), p);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getClickedInventory() != null) {
            if (e.getInventory().getName().contains("§9장비창 - ")) {
                if (e.getInventory().getName().equalsIgnoreCase("§9장비창 - " + p.getName())) {
                    if (e.getCurrentItem() != null) {
                        if (e.getCurrentItem().getType() == Material.STAINED_GLASS_PANE ||
                                e.getCurrentItem().getType() == Material.GOLD_SPADE) {
                            e.setCancelled(true);
                        }
                    }
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }


    // Commands
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("장비")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                equipment_window(p, p);
            }
        }
        return false;
    }
}
