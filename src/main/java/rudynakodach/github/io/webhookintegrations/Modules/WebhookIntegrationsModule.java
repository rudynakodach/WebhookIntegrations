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
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public abstract class WebhookIntegrationsModule {
    private static final List<WebhookIntegrationsModule> moduleInstances = new ArrayList<>();

    private final String configFileName;
    protected final JavaPlugin plugin;
    public YamlConfiguration config;

    protected WebhookIntegrationsModule(String fileName, JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFileName = fileName;

        moduleInstances.add(this);
    }

    public static void reloadAll() {
        for (WebhookIntegrationsModule module : moduleInstances) {
            module.reload();
        }
    }

    public final void reload() {
        plugin.getLogger().log(Level.INFO, "reloading %s".formatted(configFileName));
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), configFileName));
    }

    public final void reset() {
        plugin.getLogger().log(Level.INFO, "resetting %s".formatted(configFileName));
        plugin.saveResource(configFileName, true);
        reload();
    }

    public static void resetAll() {
        for (WebhookIntegrationsModule module : moduleInstances) {
            module.reset();
        }
    }

    public YamlConfiguration getYamlConfig() {
        return config;
    }

    public boolean save() {
        try {
            config.save(Path.of(plugin.getDataFolder().toString(), configFileName).toString());
            return true;
        } catch (IOException err) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save config: " + err.getMessage());
            err.printStackTrace();
            return false;
        }
    }
}
