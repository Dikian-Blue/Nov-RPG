package dikian.blue;

import dikian.blue.files.Config;
import dikian.blue.systems.*;
import dikian.blue.files.PetConfig;
import dikian.blue.http.HttpServerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Nov_RPG extends JavaPlugin {

    public static String preffix = "§e<시스템> §f";

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage("§aPlugin was Enabled!");

        HttpServerManager.main();

        PetConfig.setup();
        PetConfig.get().options().copyDefaults(true);
        PetConfig.save();
        Config.setup();
        Config.get().options().copyDefaults(true);
        Config.get().addDefault("Max-Imoji", 0);
        Config.get().addDefault("Spawn Location", "world,0,0,0");
        Config.save();
        NPC.setup();
        NPC.get().options().copyDefaults(true);
        NPC.save();

        String[] pet = {"펫","펫관리","rl_pet"};
        for (String data: pet) { Bukkit.getPluginCommand(data).setExecutor(new Pet()); }
        Bukkit.getPluginCommand("스텟").setExecutor(new Stat());
        Bukkit.getPluginCommand("장비").setExecutor(new Equipment());
        Bukkit.getPluginCommand("e").setExecutor(new Emoji());
        String[] money = {"exchange","돈","ehs","캐시","zotl"};
        for (String data: money) { Bukkit.getPluginCommand(data).setExecutor(new Currency()); }
        Bukkit.getPluginCommand("npc").setExecutor(new NPC());


        Bukkit.getPluginManager().registerEvents(new NPC(), this);
        Bukkit.getPluginManager().registerEvents(new Pet(), this);
        Bukkit.getPluginManager().registerEvents(new Board(), this);
        Bukkit.getPluginManager().registerEvents(new Stat(), this);
        Bukkit.getPluginManager().registerEvents(new Equipment(), this);
        Bukkit.getPluginManager().registerEvents(new Emoji(), this);
        Bukkit.getPluginManager().registerEvents(new Currency(), this);
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("§cPlugin was Disabled!");
        HttpServerManager.stop(0);
    }
}
