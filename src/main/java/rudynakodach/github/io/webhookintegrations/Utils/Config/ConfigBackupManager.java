package rudynakodach.github.io.webhookintegrations.Utils.Config;

import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Modules.LanguageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;

public class ConfigBackupManager {

    private final JavaPlugin plugin;
    private final Path backups;

    private static ConfigBackupManager instance;

    public ConfigBackupManager(JavaPlugin plugin) {
        this.plugin = plugin;
        backups = Path.of(plugin.getDataFolder().getAbsolutePath(), "config-backups");

        instance = this;
    }

    public static ConfigBackupManager get() {
        return instance;
    }

    public boolean saveBackup(String name) {
        Path originalConfig = Path.of(plugin.getDataFolder().getAbsolutePath(), "config.yml");
        Path originalLanguage = Path.of(plugin.getDataFolder().getAbsolutePath(), "lang.yml");
        Path originalMessages = Path.of(plugin.getDataFolder().getAbsolutePath(), "messages.yml");

        Path backupConfig = Path.of(plugin.getDataFolder().getAbsolutePath(), "config-backups", name, "config.yml");
        Path backupLanguage = Path.of(plugin.getDataFolder().getAbsolutePath(), "config-backups", name, "lang.yml");
        Path backupMessages = Path.of(plugin.getDataFolder().getAbsolutePath(), "config-backups", name, "messages.yml");

        try {
            Files.createDirectory(Path.of(plugin.getDataFolder().getAbsolutePath(), "config-backups", name));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to make a backup folder: " + e.getMessage());
            return false;
        }

        try {
            Files.copy(originalConfig, backupConfig, StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to make a copy of the config file: " + e.getMessage());
        }
        try {
            Files.copy(originalLanguage, backupLanguage, StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to make a copy of the language configuration file: " + e.getMessage());
        }
        try {
            Files.copy(originalMessages, backupMessages, StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to make a copy of JSON payloads file: " + e.getMessage());
        }

        return true;
    }

    public void loadBackup(String name) {
        Path originalConfig = Path.of(plugin.getDataFolder().getAbsolutePath(), "config.yml");
        Path originalLanguage = Path.of(plugin.getDataFolder().getAbsolutePath(), "lang.yml");
        Path originalMessages = Path.of(plugin.getDataFolder().getAbsolutePath(), "messages.yml");

        Path backupConfig = Path.of(plugin.getDataFolder().getAbsolutePath(), "config-backups", name, "config.yml");
        Path backupLanguage = Path.of(plugin.getDataFolder().getAbsolutePath(), "config-backups", name, "lang.yml");
        Path backupMessages = Path.of(plugin.getDataFolder().getAbsolutePath(), "config-backups", name, "messages.yml");

        try {
            if(Files.exists(Path.of(plugin.getDataFolder().getAbsolutePath(), "config.yml"))) {
                Files.copy(backupConfig, originalConfig, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(backupConfig, originalConfig, StandardCopyOption.COPY_ATTRIBUTES);
            }
            plugin.reloadConfig();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to replace the config file: " + e.getMessage());
        }

        try {
            if(Files.exists(Path.of(plugin.getDataFolder().getAbsolutePath(), "lang.yml"))) {
                Files.copy(backupLanguage, originalLanguage, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(backupLanguage, originalLanguage, StandardCopyOption.COPY_ATTRIBUTES);
            }
            LanguageConfiguration.get().reload();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to replace the language file: " + e.getMessage());
        }

        try {
            if(Files.exists(Path.of(plugin.getDataFolder().getAbsolutePath(), "messages.yml"))) {
                Files.copy(backupMessages, originalMessages, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(backupMessages, originalMessages, StandardCopyOption.COPY_ATTRIBUTES);
            }
            MessageConfiguration.get().reload();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to replace the messages config file: " + e.getMessage());
        }
    }

    public boolean backupExists(String name) {
        return Files.exists(Path.of(backups.toAbsolutePath().toString(), name));
    }
}
