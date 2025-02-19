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
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TemplateConfiguration extends WebhookIntegrationsModule {
    private static TemplateConfiguration instance;
    public static TemplateConfiguration get() {
        return instance;
    }

    public TemplateConfiguration(JavaPlugin plugin) {
        super("templates.yml", plugin);
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "templates.yml"));

        instance = this;
    }

    public Template getTemplate(String templateName) {
        ConfigurationSection sect = config.getConfigurationSection("templates.%s".formatted(templateName));
        if(sect == null) {
            return null;
        }
        return new Template(plugin, sect);
    }

    public class Template {
        private final JavaPlugin plugin;

        public final boolean isUsingGlobals;
        private String json;
        public final List<String> params;
        private String target;

        private Template(JavaPlugin plugin, ConfigurationSection sect) {
            this.plugin = plugin;

            this.isUsingGlobals = sect.getBoolean("useGlobals");
            this.json = sect.getString("messageJson");
            this.params = sect.getStringList("params");
            this.target = sect.getString("defaultTarget");
        }

        public String getJson() {
            return json;
        }

        public Template setJson(String json) {
            this.json = json;

            return this;
        }

        public String getTarget() {
            return target;
        }

        public Template setTarget(String target) {
            this.target = target;

            return this;
        }

        public JavaPlugin getPlugin() {
            return plugin;
        }

        public static boolean templateExists(String name) {
            return TemplateConfiguration.get().config.getConfigurationSection("templates.%s".formatted(name)) != null;
        }

        public String compile(Map<String, String> args) {
            String compiledBody = json;

            for(Map.Entry<String, String> entry : args.entrySet()) {
                if(params.contains(entry.getKey())) {
                    compiledBody = compiledBody.replace("%%%s%%".formatted(entry.getKey()), entry.getValue());
                }
            }

            if(isUsingGlobals) {
                ConfigurationSection globalsSect = config.getConfigurationSection("globals");
                if(globalsSect != null) {
                    for (String globalEntry : globalsSect.getKeys(false)) {
                        compiledBody = compiledBody.replace("$%s$".formatted(globalEntry), Objects.requireNonNull(globalsSect.getString(globalEntry)));
                    }
                }
            }

            return compiledBody;
        }
    }
}
