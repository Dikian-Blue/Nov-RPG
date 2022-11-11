package dikian.blue.systems;

import dikian.blue.Nov_RPG;
import dikian.blue.files.CurrencyFile;
import dikian.blue.files.StatUserFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Currency implements CommandExecutor, Listener {
    final String op_money_error = Nov_RPG.preffix + "/돈 <player> [<증가><차감><설정><확인>] <int>";
    final String money_error = Nov_RPG.preffix + "/돈 <player> 송금 <숫자>";
    final String cash_error = "/캐시 <player> [<증가><차감><설정><확인>] <int>";

    public void check_bank_book(Player p, Player target) {
        p.sendMessage(Nov_RPG.preffix + "§f현재 §6" + target.getDisplayName() + "§f님의 통장에는 §6" + CurrencyFile.get().getInt("Money") + "§f원이 있습니다.");
    }
    public void check_cash_bank_book(Player p, Player target) {
        p.sendMessage(Nov_RPG.preffix + "§f현재 §6" + target.getDisplayName() + "§f님의 캐시 통장에는 §6" + CurrencyFile.get().getInt("Cash") + "§f원이 있습니다.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("exchange")) {
            if (sender instanceof ConsoleCommandSender) {
                if (args.length == 1) {
                    for (Player p: Bukkit.getOnlinePlayers()) {
                        if (args[0].equals(p.getDisplayName())) {
                            Inventory inv = Bukkit.createInventory(null, 9 * 6, "§6거래소");
                            p.openInventory(inv);
                            break;
                        }
                    }
                }
            }
        }

        if (label.equalsIgnoreCase("돈") || label.equalsIgnoreCase("ehs")) {
            try {
                Player p = (Player) sender;
                if (args.length == 0) {
                    CurrencyFile.uuid = p.getUniqueId() + "";
                    CurrencyFile.setup();
                    check_bank_book(p, p);
                } else {
                    boolean check = false;
                    Player target = null;
                    for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                        if (args[0].equals(pl.getDisplayName())) {
                            target = pl;
                            check = true;
                            break;
                        }
                    }
                    if (check) {
                        if (args.length == 1) {
                            if (p.isOp()) {
                                CurrencyFile.uuid = target.getUniqueId() + "";
                                CurrencyFile.setup();
                                check_bank_book(p, target);
                            } else {
                                CurrencyFile.uuid = p.getUniqueId() + "";
                                CurrencyFile.setup();
                                check_bank_book(p, p);
                            }
                        } else if (args.length == 2) {
                            if (p.isOp()) {
                                if (args[1].equals("확인")) {
                                    CurrencyFile.uuid = target.getUniqueId() + "";
                                    CurrencyFile.setup();
                                    check_bank_book(p, target);
                                } else {
                                    p.sendMessage(op_money_error);
                                }
                            } else {
                                CurrencyFile.uuid = p.getUniqueId() + "";
                                CurrencyFile.setup();
                                check_bank_book(p, p);
                            }
                        } else if (args.length == 3) {
                            p.sendMessage("Fail");
                            int arg3 = Integer.parseInt(args[2]);
                            CurrencyFile.uuid = target.getUniqueId() + "";
                            CurrencyFile.setup();
                            if (args[1].equals("송금") || args[1].equals("thdrma")) {
                                p.sendMessage("Fail");
                                if (arg3 > 0) {
                                    p.sendMessage("Fail");
                                    CurrencyFile.uuid = p.getUniqueId() + "";
                                    CurrencyFile.setup();
                                    if (CurrencyFile.get().getInt("Money") >= arg3) {
                                        p.sendMessage("Fail");
                                        CurrencyFile.get().set("Money", CurrencyFile.get().getInt("Money") - arg3);
                                        CurrencyFile.save();
                                        CurrencyFile.uuid = target.getUniqueId() + "";
                                        CurrencyFile.setup();
                                        CurrencyFile.get().set("Money", CurrencyFile.get().getInt("Money") + arg3);
                                        CurrencyFile.save();
                                        p.sendMessage(Nov_RPG.preffix + "§6" + target.getDisplayName() + "§f님에게 §6" + arg3 + "원§f을 송금했습니다.");
                                        target.sendMessage(Nov_RPG.preffix + "§6" + p.getDisplayName() + "§f님이 §6" + arg3 + "원§f을 송금했습니다.");
                                    }
                                }
                            }
                            if (p.isOp()) {
                                if (args[1].equals("증가") || args[1].equals("wmdrk") || args[1].equals("설정") || args[1].equals("tjfwjd") ||
                                        args[1].equals("차감") || args[1].equals("ckrka") || args[1].equals("송금") || args[1].equals("thdrma")) {
                                    if (args[1].equals("증가") || args[1].equals("wmdrk")) {
                                        int money = CurrencyFile.get().getInt("Money");
                                        CurrencyFile.get().set("Money", money + arg3);
                                        CurrencyFile.save();
                                        p.sendMessage(Nov_RPG.preffix + "§6" + target.getDisplayName() + "§f님의 통장에 §6" + arg3 + "원§f을 추가했습니다.");
                                        check_bank_book(p, target);
                                    }
                                    if (args[1].equals("설정") || args[1].equals("tjfwjd")) {
                                        CurrencyFile.get().set("Money", arg3);
                                        CurrencyFile.save();
                                        p.sendMessage(Nov_RPG.preffix + "§6" + target.getDisplayName() + "§f님의 통장을 §6" + arg3 + "원§f으로 설정했습니다.");
                                        check_bank_book(p, target);
                                    }
                                    if (args[1].equals("차감") || args[1].equals("ckrka")) {
                                        int money = CurrencyFile.get().getInt("Money");
                                        CurrencyFile.get().set("Money", money - arg3);
                                        CurrencyFile.save();
                                        p.sendMessage(Nov_RPG.preffix + "§6" + target.getDisplayName() + "§f님의 통장에 §6" + arg3 + "원§f을 차감했습니다.");
                                        check_bank_book(p, target);
                                    }
                                } else {
                                    p.sendMessage(op_money_error);
                                }
                            }
                        }
                    } else {
                        p.sendMessage(op_money_error);
                    }
                }
            } catch (Exception e) {
                if (sender.isOp()) {
                    sender.sendMessage(op_money_error);
                } else {
                    sender.sendMessage(money_error);
                }
            }
        }


        if (label.equalsIgnoreCase("캐시") || label.equalsIgnoreCase("zotnl")) {
            try {
                Player p = (Player) sender;
                if (args.length == 0) {
                    CurrencyFile.uuid = p.getUniqueId() + "";
                    CurrencyFile.setup();
                    check_cash_bank_book(p, p);
                } else {
                    if (p.isOp()) {
                        boolean check = false;
                        Player target = null;
                        for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                            if (args[0].equals(pl.getDisplayName())) {
                                target = pl;
                                check = true;
                                break;
                            }
                        }
                        if (check) {
                            if (args.length == 1) {
                                CurrencyFile.uuid = target.getUniqueId() + "";
                                CurrencyFile.setup();
                                check_cash_bank_book(p, target);
                            } else if (args.length == 2) {
                                if (args[1].equals("확인")) {
                                    CurrencyFile.uuid = target.getUniqueId() + "";
                                    CurrencyFile.setup();
                                    check_cash_bank_book(p, target);
                                } else {
                                    p.sendMessage(cash_error);
                                }
                            } else if (args.length == 3) {
                                int arg3 = Integer.parseInt(args[2]);
                                CurrencyFile.uuid = target.getUniqueId() + "";
                                CurrencyFile.setup();
                                if (args[1].equals("증가") || args[1].equals("wmdrk")) {
                                    int money = CurrencyFile.get().getInt("Cash");
                                    CurrencyFile.get().set("Cash", money + arg3);
                                    CurrencyFile.save();
                                    p.sendMessage(Nov_RPG.preffix + "§6" + target.getDisplayName() + "§f님의 캐시 통장에 §6" + arg3 + "캐시§f를 추가했습니다.");
                                    check_cash_bank_book(p, target);
                                }
                                if (args[1].equals("설정") || args[1].equals("tjfwjd")) {
                                    CurrencyFile.get().set("Cash", arg3);
                                    CurrencyFile.save();
                                    p.sendMessage(Nov_RPG.preffix + "§6" + target.getDisplayName() + "§f님의 캐시 통장을 §6" + arg3 + "캐시§f로 설정했습니다.");
                                    check_cash_bank_book(p, target);
                                }
                                if (args[1].equals("차감") || args[1].equals("ckrka")) {
                                    int money = CurrencyFile.get().getInt("Cash");
                                    CurrencyFile.get().set("Cash", money - arg3);
                                    CurrencyFile.save();
                                    p.sendMessage(Nov_RPG.preffix + "§6" + target.getDisplayName() + "§f님의 캐시 통장에 §6" + arg3 + "캐시§f를 차감했습니다.");
                                    check_cash_bank_book(p, target);
                                }
                            }
                        } else {
                            p.sendMessage(cash_error);
                        }
                    }
                }
            } catch (Exception e) {
                sender.sendMessage(cash_error);
            }
        }
        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        CurrencyFile.uuid = p.getUniqueId() + "";
        CurrencyFile.setup();
        CurrencyFile.get().options().copyDefaults(true);
        CurrencyFile.get().addDefault("Money", 0);
        CurrencyFile.get().addDefault("Cash", 0);
        CurrencyFile.save();
    }
}
