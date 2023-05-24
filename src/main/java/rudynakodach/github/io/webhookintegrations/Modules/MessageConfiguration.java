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

    public YamlConfiguration getYamlConfig() {
        return config;
    }
    public String getMessage(String path) {
        return config.getString(path + ".messageJson");
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.json"));
    }
}
