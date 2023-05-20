package rudynakodach.github.io.webhookintegrations.Modules;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class LanguageConfiguration {

    private static LanguageConfiguration instance;
    private String locale;
    private YamlConfiguration languageFile;
    private final JavaPlugin plugin;
    public static LanguageConfiguration get() {
        return instance;
    }

    public LanguageConfiguration(JavaPlugin plugin, String locale, YamlConfiguration languageFile) {
        this.plugin = plugin;

        this.locale = locale;
        this.languageFile = languageFile;

        instance = this;
    }

    public void reload() {
        this.languageFile = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "lang.yml"));
    }

    public YamlConfiguration getYamlConfig() {
        return languageFile;
    }

    public void setLanguage(String locale) {
        this.locale = locale;
    }

    public String getString(String key) {
        return languageFile.getString(locale + "." + key);
    }
}
