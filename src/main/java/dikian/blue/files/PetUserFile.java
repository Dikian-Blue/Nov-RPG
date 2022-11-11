package dikian.blue.files;

import dikian.blue.Nov_RPG;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PetUserFile {

    private static File file;
    private static FileConfiguration customFile;
    public static String uuid = null;

    public static void setup() {
        file = new File(Bukkit.getPluginManager().getPlugin("Nov_RPG").getDataFolder() + "/pet/UserData", uuid + ".yml");
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
}
