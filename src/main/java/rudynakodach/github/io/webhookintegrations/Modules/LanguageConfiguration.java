package rudynakodach.github.io.webhookintegrations.Modules;

import org.bukkit.configuration.file.YamlConfiguration;

public class LanguageConfiguration {

    private static LanguageConfiguration instance;
    private String locale;
    private YamlConfiguration languageFile;

    public static LanguageConfiguration get() {
        return instance;
    }

    public LanguageConfiguration(String locale, YamlConfiguration languageFile) {
        this.locale = locale;
        this.languageFile = languageFile;

        instance = this;
    }

    public void reload(YamlConfiguration languageFile) {
        this.languageFile = languageFile;
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
