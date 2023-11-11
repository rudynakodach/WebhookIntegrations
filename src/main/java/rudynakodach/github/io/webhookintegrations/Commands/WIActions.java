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

package rudynakodach.github.io.webhookintegrations.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rudynakodach.github.io.webhookintegrations.AutoUpdater;
import rudynakodach.github.io.webhookintegrations.Modules.LanguageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageType;
import rudynakodach.github.io.webhookintegrations.WebhookIntegrations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class WIActions implements CommandExecutor, TabCompleter {
    JavaPlugin plugin;
    private final LanguageConfiguration language;
    public WIActions(JavaPlugin plugin) {
        this.plugin = plugin;

        language = LanguageConfiguration.get();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("wi")) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("reset")) {
                    if(args.length < 2) {
                        commandSender.sendMessage("/wi reset confirm");
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("confirm")) {
                        return resetConfig(commandSender);
                    } else {
                        if(!commandSender.hasPermission("webhookintegrations.config.reset")) {
                            commandSender.sendMessage(
                                    ChatColor.translateAlternateColorCodes('&',
                                            language.getString("commands.no-permission"))
                            );
                            return true;
                        }
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', language.getString("commands.config.noConfirm")));
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    return reload(commandSender);
                } else if(args[0].equalsIgnoreCase("analyze")) {
                    return analyze(commandSender);
                } else if (args[0].equalsIgnoreCase("update")) {
                    return update(commandSender);
                } else if(args[0].equalsIgnoreCase("enable")) {
                    return enable(commandSender);
                } else if(args[0].equalsIgnoreCase("disable")) {
                    return disable(commandSender);
                } else if(args[0].equalsIgnoreCase("setlanguage")) {
                    return setLanguage(commandSender, args);
                } else if(args[0].equalsIgnoreCase("config")) {
                    if(args.length < 2) {
                        commandSender.sendMessage("/wi config setvalue|savebackup|loadbackup");
                        return true;
                    }
                    if(args[1].equalsIgnoreCase("setvalue") && args.length >= 3) {
                        return setConfig(commandSender, args);
                    } else if(args[1].equalsIgnoreCase("savebackup")) {
                        return saveBackup(commandSender, args);
                    } else if(args[1].equalsIgnoreCase("loadbackup") && args.length >= 3) {
                        return loadBackup(commandSender, args);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("wi")) {
            if (args.length == 1) {
                suggestions.add("setlanguage");
                suggestions.add("reset");
                suggestions.add("enable");
                suggestions.add("disable");
                suggestions.add("reload");
                suggestions.add("analyze");
                suggestions.add("update");
                suggestions.add("config");
            } else if (args.length == 2) {
                if(args[0].equalsIgnoreCase("reset")) {
                    suggestions.add("confirm");
                } else if(args[0].equalsIgnoreCase("setlanguage")) {
                    return language.getYamlConfig().getKeys(false).stream().toList();
                } else if(args[0].equalsIgnoreCase("config")) {
                    suggestions.add("setvalue");
                    suggestions.add("savebackup");
                    suggestions.add("loadbackup");
                    return suggestions;
                }
            } else if(args.length == 3) {
                if(args[0].equalsIgnoreCase("config")) {
                    if(args[1].equalsIgnoreCase("setvalue")) {
                        return plugin.getConfig().getKeys(true).stream().toList();
                    } else if(args[1].equalsIgnoreCase("loadbackup")) {
                        File configBackupsDirectory = new File(plugin.getDataFolder(), "config-backups");
                        File[] backups = configBackupsDirectory.listFiles();
                        if(backups == null) {
                            return suggestions;
                        }

                        // Returns a list of all filenames present in the config-backups directory.
                        return Arrays.stream(backups)
                                .map(File::getName)
                                .collect(Collectors.toList());
                    }
                }
            } else if(args.length == 4) {
                if(args[0].equalsIgnoreCase("config") && args[1].equalsIgnoreCase("setvalue")) {
                    if(!plugin.getConfig().contains(args[3])) {return null;}
                    Object value = plugin.getConfig().get(args[3]);
                    if(value instanceof Boolean) {
                        suggestions.add("true");
                        suggestions.add("false");
                        return suggestions;
                    }
                }
            }
        }
        return suggestions;
    }

    @Contract(pure = true)
    private @NotNull String colorBoolean(Boolean b) {
        return (b ? ChatColor.GREEN : ChatColor.RED) + String.valueOf(b) + ChatColor.RESET;
    }

    private boolean analyze(CommandSender commandSender) {
        if (!commandSender.hasPermission("webhookintegrations.analyze")) {
            commandSender.sendMessage(
                    ChatColor.translateAlternateColorCodes('&',
                            language.getString("commands.no-permission"))
            );
            return true;
        }
        commandSender.sendMessage(ChatColor.AQUA + "Analyzing config... To reload the config use /wi reload");
        String message = "auto-update: " + colorBoolean(plugin.getConfig().getBoolean("auto-update"));
        if (Objects.requireNonNull(plugin.getConfig().getString("webhookUrl")).trim().equalsIgnoreCase("")) {
            message += "\nwebhookUrl: " + ChatColor.RED + "unset\n";
        } else {
            message += "\nwebhookUrl: " + ChatColor.GREEN + "set\n";
        }

        message += ChatColor.YELLOW + "EVENTS" + ChatColor.WHITE;
        message += "\nServer Start: " + colorBoolean(MessageConfiguration.get().getYamlConfig().getBoolean(MessageType.SERVER_START));
        message += "\nServer Stop: " + colorBoolean(MessageConfiguration.get().getYamlConfig().getBoolean(MessageType.SERVER_STOP));
        message += "\nPlayer Join: " + colorBoolean(MessageConfiguration.get().getYamlConfig().getBoolean(MessageType.PLAYER_JOIN));
        message += "\nPlayer Quit: " + colorBoolean(MessageConfiguration.get().getYamlConfig().getBoolean(MessageType.PLAYER_QUIT));
        message += "\nPlayer Kick: " + colorBoolean(MessageConfiguration.get().getYamlConfig().getBoolean(MessageType.PLAYER_KICK));
        message += "\nAdvancement made: " + colorBoolean(MessageConfiguration.get().getYamlConfig().getBoolean(MessageType.PLAYER_ADVANCEMENT));
        message += "\nPlayer Death PVE: " + colorBoolean(MessageConfiguration.get().getYamlConfig().getBoolean(MessageType.PLAYER_DEATH_NPC));
        message += "\nPlayer Death PVP: " + colorBoolean(MessageConfiguration.get().getYamlConfig().getBoolean(MessageType.PLAYER_DEATH_KILLED));

        commandSender.sendMessage(message);
        return true;
    }

    private boolean resetConfig(CommandSender commandSender) {
        if (!commandSender.hasPermission("webhookintegrations.config.reset")) {
            commandSender.sendMessage(
                    ChatColor.translateAlternateColorCodes('&',
                            language.getString("commands.no-permission"))
            );
            return true;
        }

        plugin.saveResource("config.yml", true);
        plugin.saveResource("lang.yml", true);
        plugin.saveResource("messages.yml", true);

        plugin.reloadConfig();
        return true;
    }

    private boolean reload(CommandSender commandSender) {
        if (!commandSender.hasPermission("webhookintegrations.reload")) {
            commandSender.sendMessage(
                    ChatColor.translateAlternateColorCodes('&',
                            language.getString("commands.no-permission"))
            );
            return true;
        }
        plugin.reloadConfig();
        language.reload();
        MessageConfiguration.get().reload();
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', language.getString("commands.config.reloadFinish")));
        return true;
    }

    private boolean update(CommandSender commandSender) {
        if(!commandSender.hasPermission("webhookintegrations.update")) {
            commandSender.sendMessage(
                    ChatColor.translateAlternateColorCodes('&',
                            language.getString("commands.no-permission"))
            );
            return true;
        }
        AutoUpdater updater = new AutoUpdater(plugin);
        try {
            int latestVersion = updater.getLatestVersion();
            if (latestVersion > WebhookIntegrations.currentBuildNumber) {
                boolean success = updater.Update();
                if (success) {
                    commandSender.sendMessage(language.getString("commands.update.success"));
                } else {
                    commandSender.sendMessage(language.getString("commands.update.failed"));
                }
            } else {
                if(latestVersion == -1) {
                    commandSender.sendMessage(language.getString("commands.update.versionCheckFailed"));
                } else {
                    commandSender.sendMessage(language.getString("commands.update.latest"));
                }
            }
        } catch (IOException ignored) {}
        return true;
    }

    private boolean enable(CommandSender commandSender) {
        if(!commandSender.hasPermission("webhookintegrations.enable")) {
            commandSender.sendMessage(
                    ChatColor.translateAlternateColorCodes('&',
                            language.getString("commands.no-permission"))
            );
            return true;
        }

        plugin.getConfig().set("isEnabled", true);
        plugin.saveConfig();
        plugin.reloadConfig();

        commandSender.sendMessage(LanguageConfiguration.get().getString("commands.enable"));

        return true;
    }

    private boolean disable(CommandSender commandSender) {
        if(!commandSender.hasPermission("webhookintegrations.disable")) {
            commandSender.sendMessage(
                    ChatColor.translateAlternateColorCodes('&',
                            language.getString("commands.no-permission"))
            );
            return true;
        }

        plugin.getConfig().set("isEnabled", false);
        plugin.saveConfig();
        plugin.reloadConfig();

        commandSender.sendMessage(LanguageConfiguration.get().getString("commands.disable"));

        return true;
    }

    private boolean setLanguage(CommandSender commandSender, String[] args) {
        if(!commandSender.hasPermission("webhookintegrations.setlanguage")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                language.getString("commands.no-permission")));
            return true;
        }

        if(args.length < 2) {
            commandSender.sendMessage("/wi setlanguage lang");
            return true;
        }

        String newLang = args[1];
        if(language.getYamlConfig().contains(newLang)) {
            plugin.getConfig().set("language-override", newLang);
            plugin.reloadConfig();
            language.setLanguage(newLang);
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', language.getString("commands.setLang.changed")));
        } else {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', language.getString("commands.setLang.notExists")));
        }
        return true;
    }

    private boolean setConfig(CommandSender commandSender, String[] args) {
        if(!commandSender.hasPermission("webhookintegrations.config.setvalue")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    language.getString("commands.no-permission")));
            return true;
        }
        String path = args[2];
        Object value = args.length >= 4 ? String.join(" ", Arrays.copyOfRange(args, 3, args.length)) : null;
        String message;
        Object oldValue = null;

        if(plugin.getConfig().contains(path)) {
            oldValue = plugin.getConfig().get(path);
            message = LanguageConfiguration.get().getString("commands.config.keyEdited");
        } else {
            message = LanguageConfiguration.get().getString("commands.config.keyCreated");
        }

        if(value == null) {
            message = LanguageConfiguration.get().getString("commands.config.keyRemoved");
        } else {
            if (value.toString().equalsIgnoreCase("true")) {
                value = true;
            } else if (value.toString().equalsIgnoreCase("false")) {
                value = false;
            } else {
                try {
                    value = Integer.parseInt(value.toString());
                } catch (NumberFormatException ignored) {}
            }
        }

        plugin.getConfig().set(path, value);
        try {
            plugin.getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (Exception e) {
            commandSender.sendMessage(LanguageConfiguration.get().getString("commands.config.saveFailed").replace("%04", e.getMessage()));
            return true;
        }

        message = message.replace("%01", path)
                .replace("%02", value instanceof String ?
                        String.format("\"%s\"", value) :
                        String.valueOf(value));

        if(oldValue != null) {
            message = message.replace("%03", oldValue.toString());
        }

        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        return true;
    }

    private boolean saveBackup(CommandSender commandSender, String[] args) {
        if(!commandSender.hasPermission("webhookintegrations.config.savebackup")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    language.getString("commands.no-permission")));
            return true;
        }
        // Saves the backup as the provided name if possible, current unix time otherwise.
        String backupName = args.length >= 3 ? String.join("_", Arrays.copyOfRange(args, 2, args.length)) :
                new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss-SSS").format(new Date());

        Path originalConfig = Path.of(plugin.getDataFolder().getAbsolutePath(), "config.yml");
        Path originalLanguage = Path.of(plugin.getDataFolder().getAbsolutePath(), "lang.yml");
        Path originalMessages = Path.of(plugin.getDataFolder().getAbsolutePath(), "messages.yml");

        Path backupConfig = Path.of(plugin.getDataFolder().getAbsolutePath(), "config-backups", backupName, "config.yml");
        Path backupLanguage = Path.of(plugin.getDataFolder().getAbsolutePath(), "config-backups", backupName, "lang.yml");
        Path backupMessages = Path.of(plugin.getDataFolder().getAbsolutePath(), "config-backups", backupName, "messages.yml");

        try {
            Files.createDirectory(Path.of(plugin.getDataFolder().getAbsolutePath(), "config-backups", backupName));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to make a backup folder: " + e.getMessage());
            return true;
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

        String message = LanguageConfiguration.get().getString("commands.config.backupCreated");
        message = message.replace("%01", backupName);
        message = ChatColor.translateAlternateColorCodes('&', message);

        commandSender.sendMessage(message);

        return true;
    }

    private boolean loadBackup(CommandSender commandSender, String[] args) {
        if(!commandSender.hasPermission("webhookintegrations.config.loadbackup")) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    language.getString("commands.no-permission")));
            return true;
        }

        String backupName = args[2];
        Path backups = Path.of(plugin.getDataFolder().getAbsolutePath(), "config-backups");

        if(!Files.exists(Path.of(backups.toAbsolutePath().toString(), backupName))) {
            commandSender.sendMessage(Component.text("Folder doesn't exist.").color(NamedTextColor.RED));
            return true;
        }

        Path originalConfig = Path.of(plugin.getDataFolder().getAbsolutePath(), "config.yml");
        Path originalLanguage = Path.of(plugin.getDataFolder().getAbsolutePath(), "lang.yml");
        Path originalMessages = Path.of(plugin.getDataFolder().getAbsolutePath(), "messages.yml");

        Path backupConfig = Path.of(plugin.getDataFolder().getAbsolutePath(), "config-backups", backupName, "config.yml");
        Path backupLanguage = Path.of(plugin.getDataFolder().getAbsolutePath(), "config-backups", backupName, "lang.yml");
        Path backupMessages = Path.of(plugin.getDataFolder().getAbsolutePath(), "config-backups", backupName, "messages.yml");

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
            plugin.getLogger().log(Level.SEVERE, "Failed to replace the language configuration file: " + e.getMessage());
        }

        try {
            if(Files.exists(Path.of(plugin.getDataFolder().getAbsolutePath(), "messages.yml"))) {
                Files.copy(backupMessages, originalMessages, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(backupMessages, originalMessages, StandardCopyOption.COPY_ATTRIBUTES);
            }
            MessageConfiguration.get().reload();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to replace the JSON payloads file: " + e.getMessage());
        }

        String message = LanguageConfiguration.get().getString("commands.config.backupLoaded");
        message = message.replace("%01", backupName);
        message = ChatColor.translateAlternateColorCodes('&', message);

        commandSender.sendMessage(message);

        return true;
    }
}
