package rudynakodach.github.io.webhookintegrations.Modules;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class MessageConfiguration {
    private YamlConfiguration config;
    private final JavaPlugin plugin;
    private static MessageConfiguration instance;

    public static MessageConfiguration get() {
        return instance;
    }

    public MessageConfiguration(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));

        instance = this;
    }


    public boolean canAnnounce(String message) {
        return config.getBoolean(message + ".announce");
    }

    public YamlConfiguration getConfig() {
        return config;
    }
    public String getMessage(String path) {
        return config.getString(path + ".messageJson");
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.json"));
    }
}
