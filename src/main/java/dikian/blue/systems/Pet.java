package dikian.blue.systems;

import dikian.blue.Nov_RPG;
import dikian.blue.files.PetConfig;
import dikian.blue.files.PetUserFile;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Pet implements CommandExecutor, Listener {

    // Config Info
    // 체력, 공격력, 마나, 이동속도, 행운, 크리티컬 확률, 크리티컬 데미지, 방어력, 레벨당 증가 비율(%)

    // Valuable
    public boolean check;
    public String pet_add = Nov_RPG.preffix + "§a/펫관리 [id] 추가 [이름] [최대레벨] [등급] [기본경험치] [레벨업당 늘어날 경험치] [내구도(리팩관련)]";
    public String pet_ability = Nov_RPG.preffix + "§a/펫관리 [id] 능력 [체력] [공격력] [마나] [이동속도] [행운] [크확] [크뎀] [방어력] [레벨당 증가 비율]";
    public String pet_manage = Nov_RPG.preffix + "§a/펫관리 [id] [추가/능력/삭제]";
    public Map<UUID, Integer> id = new HashMap<UUID, Integer>();
    public Map<UUID, ArmorStand> Stand = new HashMap<UUID, ArmorStand>();

    private float toDegree(double angle) {
        return (float) Math.toDegrees(angle);
    }


    // Events
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        PetUserFile.uuid = p.getUniqueId() + "";
        PetUserFile.setup();
        PetUserFile.save();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (Stand.get(e.getPlayer().getUniqueId()) == null) {
            //
        } else {
            Stand.get(e.getPlayer().getUniqueId()).remove();
            Stand.put(e.getPlayer().getUniqueId(), null);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (e.getInventory().getName().equalsIgnoreCase("§6펫")){
            p.sendTitle("", "", 0, 0, 0);
        }
    }

    @EventHandler
    public void onDisable(PluginDisableEvent e) {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (Stand.get(pl.getUniqueId()) != null) {
                Stand.get(pl.getUniqueId()).remove();
            }
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase("§6펫")) {
            if (e.getWhoClicked() instanceof Player) {
                Player p = (Player) e.getWhoClicked();
                e.setCancelled(true);
                if (e.getCurrentItem() != null) {
                    if (e.getCurrentItem().getType() == Material.DIAMOND_SPADE) {
                        if (e.getCurrentItem().getDurability() == (short) 1) {
                            p.sendMessage(Nov_RPG.preffix + "§8업데이트 예정입니다.");
                        }
                        if (e.getCurrentItem().getDurability() == (short) 2) {
                            p.sendMessage(Nov_RPG.preffix + "§c보유중이지 않은 펫입니다.");
                        }
                        for (int i = 0; i < 54; i++) {
                            if (PetConfig.get().getString(i + "") != null) {
                                String[] data = PetConfig.get().getString(i + "").split(", ");
                                try {
                                    if (e.getCurrentItem().getDurability() == (short) Integer.parseInt(data[5])) {
                                        if (e.getClick().isLeftClick()) {

                                            if (Stand.get(p.getUniqueId()) == null) {
                                                //
                                            } else {
                                                Stand.get(p.getUniqueId()).remove();
                                                Stand.put(p.getUniqueId(), null);
                                            }
                                            ArmorStand stand = (ArmorStand) p.getWorld().spawnEntity(new Location(p.getWorld(), p.getLocation().getX()
                                                    , p.getLocation().getY() + 100, p.getLocation().getZ()), EntityType.ARMOR_STAND);
                                            stand.setSmall(true);
                                            stand.setVisible(false);
                                            stand.setGravity(false);
                                            stand.setMarker(true);
                                            ItemStack item = new ItemStack(Material.DIAMOND_SPADE);
                                            item.setDurability((short) Integer.parseInt(data[5]));
                                            stand.setHelmet(item);
                                            stand.teleport(p.getLocation().add(0, 0.01, 0));
                                            id.put(p.getUniqueId(), i);
                                            Stand.put(p.getUniqueId(), stand);

                                            p.closeInventory();
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        if (Stand.get(p.getUniqueId()) == null) {
                                                            this.cancel();
                                                        } else {
                                                            Location armorstandLocation = Stand.get(p.getUniqueId()).getLocation();
                                                            Vector playerVec = p.getLocation().toVector();
                                                            Vector armorstandVec = armorstandLocation.toVector();
                                                            Vector facingVector = playerVec.subtract(armorstandVec).normalize();

                                                            armorstandLocation.setDirection(facingVector); // Set the direction the armour stand SHOULD be facing
                                                            Stand.get(p.getUniqueId()).teleport(armorstandLocation);
                                                            for (int i = 0; i < 2; i++) {
                                                                if (Stand.get(p.getUniqueId()).getLocation().distance(p.getLocation()) >= 4) {
                                                                    Location loc = Stand.get(p.getUniqueId()).getLocation();
                                                                    Vector dir = loc.getDirection();
                                                                    dir.normalize();
                                                                    dir.multiply(0.2);
                                                                    loc.add(dir);
                                                                    Stand.get(p.getUniqueId()).teleport(loc);
                                                                }
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        //
                                                    }
                                                }
                                            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("Nov_RPG"), 5, 1);
                                            break;
                                        }
                                        if (e.getClick().isRightClick()) {
                                            Stand.get(p.getUniqueId()).remove();
                                            Stand.put(p.getUniqueId(), null);
                                            String[] getdata = PetConfig.get().getString(id.get(p.getUniqueId()) + "").split(", ");
                                            List<String> codes1 = new ArrayList<>();
                                            List<String> codes2 = new ArrayList<>();
                                            for (int ii = 0; ii < 10; ii++) {
                                                codes1.add("&" + ii);
                                                codes2.add("§" + ii);
                                            }
                                            for (char ii = 'a'; ii <= 'f'; ii++) {
                                                codes1.add("&" + ii);
                                                codes2.add("§" + ii);
                                            }
                                            for (char ii = 'l'; ii <= 'o'; ii++) {
                                                codes1.add("&" + ii);
                                                codes2.add("§" + ii);
                                            }
                                            String name = data[0];
                                            for (int ii = 0; ii <= 18; ii++) {
                                                name = name.replace(codes1.get(ii), codes2.get(ii));
                                            }
                                            p.closeInventory();
                                            p.sendMessage(Nov_RPG.preffix + "§a" + name + "§a펫이 해제되었습니다.");
                                        }
                                    }
                                } catch (Exception error) {
                                    error.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    // Commands
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("rl_pet")) {
            if (sender.isOp()) {
                try {
                    PetConfig.reload();
                    sender.sendMessage("§a펫 콘피그 파일이 정상적으로 리로드되었습니다.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sender.sendMessage("§c§o이 명령어를 사용하기위한 권한이 부족합니다.");
            }
        }
        if (label.equalsIgnoreCase("펫")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 0) {
                    p.sendTitle("§6펫                   펫", "§6Left-Click 장착                         Right-Click 해제",
                            0, 20 * 60 * 60, 0);

                    Inventory inv = Bukkit.createInventory(null, 9 * 6, "§6펫");
                    for (int i = 0; i < 54; i++) {
                        if (PetConfig.get().getString(i + "") == null) {
                            ItemStack item = new ItemStack(Material.DIAMOND_SPADE);
                            item.setDurability((short) 1);
                            ItemMeta meta = item.getItemMeta();
                            meta.setUnbreakable(true);
                            meta.setDisplayName("§8업데이트 예정입니다.");
                            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                            List<String> lore = new ArrayList<String>();
                            lore.add("§8§o업데이트 예정입니다.");
                            meta.setLore(lore);
                            item.setItemMeta(meta);
                            inv.setItem(i, item);
                        } else {
                            PetUserFile.uuid = p.getUniqueId() + "";
                            PetUserFile.setup();
                            if (PetUserFile.get().getString(String.valueOf(i)) == null) {
                                ItemStack item = new ItemStack(Material.DIAMOND_SPADE);
                                item.setDurability((short) 2);
                                ItemMeta meta = item.getItemMeta();
                                meta.setUnbreakable(true);
                                meta.setDisplayName("§c보유중이지 않은 펫입니다.");
                                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                                List<String> lore = new ArrayList<String>();
                                lore.add("§c§o보유중이지 않은 펫입니다.");
                                meta.setLore(lore);
                                item.setItemMeta(meta);
                                inv.setItem(i, item);
                            } else {
                                String[] data = PetConfig.get().getString(i + "").split(", ");
                                ItemStack item = new ItemStack(Material.DIAMOND_SPADE);
                                item.setDurability((short) Integer.parseInt(data[5]));
                                ItemMeta meta = item.getItemMeta();
                                meta.setUnbreakable(true);
                                List<String> codes1 = new ArrayList<>();
                                List<String> codes2 = new ArrayList<>();
                                for (int ii = 0; ii < 10; ii++) {
                                    codes1.add("&" + ii);
                                    codes2.add("§" + ii);
                                }
                                for (char ii = 'a'; ii <= 'f'; ii++) {
                                    codes1.add("&" + ii);
                                    codes2.add("§" + ii);
                                }
                                for (char ii = 'l'; ii <= 'o'; ii++) {
                                    codes1.add("&" + ii);
                                    codes2.add("§" + ii);
                                }
                                String name = data[0];
                                for (int ii = 0; ii <= 18; ii++) {
                                    name = name.replace(codes1.get(ii), codes2.get(ii));
                                }
                                meta.setDisplayName(name);
                                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                                List<String> lore = new ArrayList<String>();
                                String description = PetConfig.get().getString(i + "-description");
                                for (int ii = 0; ii <= 18; ii++) {
                                    description = description.replace(codes1.get(ii), codes2.get(ii));
                                }
                                lore.add("§f" + description);
                                lore.add("");
                                String[] ability = PetConfig.get().getString(i + "-ability").split(", ");
                                lore.add("§a체력 +" + ability[0]);
                                lore.add("§c공격력 +" + ability[1]);
                                lore.add("§9마나 +" + ability[2]);
                                lore.add("§f이동속도 +" + ability[3]);
                                lore.add("§3치명타 확률 +" + ability[4]);
                                lore.add("§1치명타 데미지(%) +" + ability[5]);
                                lore.add("§2방어력 +" + ability[0]);
                                meta.setLore(lore);
                                item.setItemMeta(meta);
                                inv.setItem(i, item);
                            }
                        }
                    }
                    p.openInventory(inv);
                } else {
                    if (sender.isOp()) {
                        check = false;
                        for (String paths : PetConfig.get().getKeys(false)) {
                            if (args[0].equals(paths)) {check = true;break;}
                        }
                        if (check ) {
                            if (args.length == 3) {
                                check = false;
                                Player arg2 = p;
                                for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                                    if (args[2].equals(pl.getDisplayName())) {
                                        check = true;
                                        arg2 = pl;
                                        break;
                                    }
                                }
                                if (check) {
                                    if (args[1].equals("추가")) {
                                        if (PetConfig.get().getString(args[0] + "-ability").equals("")) {
                                            p.sendMessage(Nov_RPG.preffix + "§c능력설정이 안된 펫입니다.");
                                        } else {
                                            PetUserFile.uuid = arg2.getUniqueId() + "";
                                            PetUserFile.setup();
                                            PetUserFile.get().options().copyDefaults(true);
                                            if (PetUserFile.get().getString(args[0]) == null) {
                                                PetUserFile.get().set(args[0], "1, 0, 1");
                                            } else {
                                                String[] data = PetUserFile.get().getString(args[0]).split(", ");
                                                PetUserFile.get().set(args[0], data[0] + ", " + data[1] + ", " + Integer.parseInt(data[2]) + 1);
                                            }
                                            PetUserFile.save();
                                        }
                                    }
                                }
                            } else {
                                p.sendMessage(Nov_RPG.preffix + "§c사용중이지 않는 ID입니다.");
                            }
                        }
                    }
                }
            }
        }

        if (label.equalsIgnoreCase("펫관리")) {
            if (sender.isOp()) {
                if (0 < args.length) {
                    if (args.length >= 8) {
                        check = false;
                        for (int i = 0; i < 54; i++) {
                            if (args[0].equals(i + "")) {
                                check = true;
                                break;
                            }
                        } if (check) {
                            if (args[1].equals("추가")) {
                                if (args.length == 8) {
                                    check = false;
                                    for (String paths : PetConfig.get().getKeys(false)) {
                                        if (args[0].equals(paths)) {check = true;break;}
                                    } if (!check) {
                                        try {
                                            PetConfig.get().options().copyDefaults(true);
                                            Integer.parseInt(args[3]);
                                            Integer.parseInt(args[5]);
                                            Integer.parseInt(args[6]);
                                            Integer.parseInt(args[7]);
                                            PetConfig.get().set(args[0], args[2] + ", " + args[3] + ", " + args[4] + ", " + args[5] + ", " +
                                                    args[6] + ", " + args[7]);
                                            PetConfig.get().set(args[0] + "-description", "");
                                            PetConfig.get().set(args[0] + "-ability", "");
                                            PetConfig.save();
                                            sender.sendMessage(Nov_RPG.preffix + "§a"+ args[0] +"펫이 추가되었습니다.");
                                        } catch (Exception e) {
                                            sender.sendMessage(pet_add);
                                        }
                                    } else {
                                        sender.sendMessage(Nov_RPG.preffix + "§c이미 있는 ID입니다.");
                                    }
                                } else {
                                    sender.sendMessage(pet_add);
                                }
                            } if (args[1].equals("능력")) {
                                if (args.length == 11) {
                                    try {
                                        if (PetConfig.get().getString(args[0] + "-ability") == null) {
                                            sender.sendMessage(Nov_RPG.preffix + "§c사용중이지 않는 ID입니다.");
                                        } else {
                                            String ability = "";
                                            for (int i = 2; i <= 10; i++) {
                                                if (i == 2) {
                                                    ability = ability + args[i];
                                                } else {
                                                    ability = ability + ", " + args[i];
                                                }
                                            }
                                            sender.sendMessage(Nov_RPG.preffix + "§a"+ args[0] +"의 능력설정이 완료되었습니다.");
                                            PetConfig.get().set(args[0] + "-ability", ability);
                                            PetConfig.save();
                                        }
                                    } catch (Exception e) {
                                        sender.sendMessage(pet_ability);
                                    }
                                } else {
                                    sender.sendMessage(pet_ability);
                                }
                            }
                        } else {
                            sender.sendMessage(Nov_RPG.preffix + "§cID는 0 ~ 53까지 입력가능합니다.");
                        }
                    } else {
                        sender.sendMessage(pet_manage);
                    }
                } else {
                    sender.sendMessage(pet_manage);
                }
            }
        }
        return false;
    }
}