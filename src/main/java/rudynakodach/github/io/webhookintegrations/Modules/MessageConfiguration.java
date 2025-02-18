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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;

public class MessageConfiguration extends WebhookIntegrationsModule {
    private static MessageConfiguration instance;

    public static MessageConfiguration get() {
        return instance;
    }

    public MessageConfiguration(JavaPlugin plugin) {
        super("messages.yml", plugin);
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));

        instance = this;
    }

    public boolean canAnnounce(String message) {
        return config.getBoolean(message + ".announce");
    }

    public boolean hasPlayerPermission(Player p, String messageType) {
        if(config.getBoolean("%s.usePermissions".formatted(messageType), false)) {
            return p.hasPermission("webhookintegrations.events.%s".formatted(messageType)) || p.hasPermission("webhookintegrations.events.all");
        }

        return true;
    }

    public String getTarget(String message) {
        return config.getString("%s.target".formatted(message), "main");
    }

    public String getMessage(String path) {
        return config.getString(path + ".messageJson");
    }

    public @Nullable HashMap<String, String> getHeaders(String messageType) {
        ConfigurationSection headersSection = config.getConfigurationSection("%s.headers".formatted(messageType));

        if(headersSection == null) {
            return null;
        }

        HashMap<String, String> headers = new HashMap<>();

        for(String key : headersSection.getKeys(false)) {
            headers.put(key, headersSection.get(key).toString());
        }

        return headers;
    }
}
