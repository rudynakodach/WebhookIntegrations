/*
 * WebhookIntegrations
 * Copyright (C) 2023 rudynakodach
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    public String getLocale() {
        return locale;
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
