package rudynakodach.github.io.webhookintegrations.Utils.Config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import rudynakodach.github.io.webhookintegrations.Modules.MessageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageType;
import rudynakodach.github.io.webhookintegrations.Modules.TemplateConfiguration;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class ConfigMigrator {
    public static void migrate(@NotNull JavaPlugin plugin, int current, int target) {
        plugin.getLogger().log(Level.INFO, "Migrating config from %d to %d".formatted(current, target));

        while (current < target) {
            switch (current) {
                case 1:
                    toVersion2(plugin);
                    break;
                case 2:
                    toVersion3(plugin);
                    break;
                case 3:
                    toVersion4(plugin);
                    break;
            }
            current++;
        }
    }

    private static void toVersion2(@NotNull JavaPlugin plugin) {
        String webhookUrl = plugin.getConfig().getString("webhookUrl");
        if(webhookUrl != null) {
            if(!webhookUrl.isEmpty()) {
                plugin.getConfig().set("webhooks.main", webhookUrl);
            }
        }

        MessageConfiguration messageConfiguration = MessageConfiguration.get();
        for(String messageType : MessageType.getAllMessageTypes()) {
            ConfigurationSection sect = messageConfiguration.getYamlConfig().getConfigurationSection(messageType);

            if(sect != null) {
                sect.set("target", "main");
            }
        }
        if(!messageConfiguration.save()) {
            plugin.getLogger().log(Level.WARNING, "Failed to save message config file during migration");
        }
        messageConfiguration.reload();

        TemplateConfiguration templateConfiguration = TemplateConfiguration.get();
        ConfigurationSection templates = templateConfiguration.getYamlConfig().getConfigurationSection("templates");

        if(templates != null) {
            for(String temp : templates.getKeys(false)) {
                templates.set("%s.defaultTarget".formatted(temp), "main");
            }
        }
        if(!templateConfiguration.save()) {
            plugin.getLogger().log(Level.INFO, "Failed to save template config file during migration");
        }
        templateConfiguration.reload();

        plugin.getConfig().set("remove-color-coding", false);
        plugin.getConfig().set("color-coding-regex", "[&§][a-f0-9klmnor]|&?#[0-9a-f]{6}");

        plugin.getConfig().set("config-version", 2);
        plugin.saveConfig();
        plugin.reloadConfig();

        plugin.getLogger().log(Level.INFO, "Config migrated to version 2!");
    }

    private static void toVersion3(@NotNull JavaPlugin plugin) {
        plugin.getConfig().set("send-quit-when-kicked", false);
        plugin.getConfig().set("timeout-delay", 0);
        plugin.getConfig().set("ignore-events-during-timeout", true);

        YamlConfiguration newMessageConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("messages.yml"), StandardCharsets.UTF_8));

        MessageConfiguration.get().getYamlConfig().set("onPlayerCountChange", newMessageConfig.getConfigurationSection("onPlayerCountChange"));
        MessageConfiguration.get().save();
        MessageConfiguration.get().reload();

        plugin.getConfig().set("config-version", 3);
        plugin.saveConfig();
        plugin.reloadConfig();

        plugin.getLogger().log(Level.INFO, "Config migrated to version 3!");
    }

    public static void toVersion4(@NotNull JavaPlugin plugin) {
        plugin.getConfig().set("exclude-vanished-from-player-count", true);

        plugin.getConfig().set("config-version", 4);
        plugin.saveConfig();
        plugin.reloadConfig();

        plugin.getLogger().log(Level.INFO, "Config migrated to version 4!");
    }
}
