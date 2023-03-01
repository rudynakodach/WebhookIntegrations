package rudynakodach.github.io.webhookintegrations.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.AutoUpdater;
import rudynakodach.github.io.webhookintegrations.WebhookIntegrations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class WIActions implements CommandExecutor, TabCompleter {
    JavaPlugin plugin;
    public WIActions(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            plugin.getLogger().log(Level.INFO, "This command is intended to be used in the game.");
            return true;
        }
        Player player = (Player) commandSender;
        if(command.getName().equalsIgnoreCase("wi")) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("reset")) {
                    if (args[1].equalsIgnoreCase("confirm")) {
                        if (player.hasPermission("webhookintegrations.config.reset")) {
                            plugin.saveResource("config.yml", true);
                            plugin.reloadConfig();
                            return true;
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebhookIntegrations.lang.getString(WebhookIntegrations.localeLang + ".commands.config.noConfirm")));
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (player.hasPermission("webhookintegrations.reload")) {
                        plugin.reloadConfig();
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', WebhookIntegrations.lang.getString(WebhookIntegrations.localeLang + ".commands.config.reloadFinish")));
                        return true;
                    }
                } else if(args[0].equalsIgnoreCase("analyze")) {
                    if(player.hasPermission("webhookintegrations.config.analyze")) {
                        commandSender.sendMessage(ChatColor.AQUA + "Analyzing config... To reload the config use /wi reload");
                        StringBuilder message = new StringBuilder("auto-update: " + colorBoolean(plugin.getConfig().getBoolean("auto-update")));
                        if (plugin.getConfig().getString("webhookUrl").trim().equalsIgnoreCase("")) {
                            message.append("\nwebhookUrl: " + ChatColor.RED + "unset\n");
                        } else {
                            message.append("\nwebhookUrl: " + ChatColor.GREEN + "set\n");
                        }
                        message.append(ChatColor.YELLOW + "EVENTS");
                        message.append("\nonStart: " + colorBoolean(plugin.getConfig().getBoolean("onServerStart.announce")));
                        message.append("\nonStop: " + colorBoolean(plugin.getConfig().getBoolean("onServerStop.announce")));
                        message.append("\nplayerJoin: " + colorBoolean(plugin.getConfig().getBoolean("onPlayerJoin.announce")));
                        message.append("\nplayerQuit: " + colorBoolean(plugin.getConfig().getBoolean("onPlayerQuit.announce")));
                        message.append("\nplayerKicked: " + colorBoolean(plugin.getConfig().getBoolean("onPlayerKicked.announce")));
                        message.append("\nonAdvancementMade: " + colorBoolean(plugin.getConfig().getBoolean("onPlayerAdvancementComplete.announce")));
                        message.append("\nplayerDeathPve: " + colorBoolean(plugin.getConfig().getBoolean("onPlayerDeath.playerKilledByNPC.announce")));
                        message.append("\nPlayerDeathPvp: " + colorBoolean(plugin.getConfig().getBoolean("onPlayerDeath.playerKilledByPlayer.announce")));

                        commandSender.sendMessage(String.valueOf(message));
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("update")) {
                    AutoUpdater updater = new AutoUpdater(plugin);
                    try {
                        if (updater.getLatestVersion() > WebhookIntegrations.currentBuildNumber) {
                            boolean success = updater.Update();
                            if (success) {
                                commandSender.sendMessage(WebhookIntegrations.lang.getString(WebhookIntegrations.localeLang + ".commands.update.success"));
                            } else {
                                commandSender.sendMessage(WebhookIntegrations.lang.getString(WebhookIntegrations.localeLang + ".commands.update.failed"));
                            }
                        }
                    } catch (IOException ignored) {}
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if(command.getName().equalsIgnoreCase("wi")) {

            if (args.length == 0) {
                suggestions.add("reset");
                suggestions.add("reload");
                suggestions.add("analyze");
                return suggestions;
            } else {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reset")) {
                        suggestions.add("confirm");
                        return suggestions;
                    }
                }
            }
        }
        return null;
    }
    private String colorBoolean(Boolean b) {
        if(!b) {
            return ChatColor.RED + String.valueOf(false);
        }
        else {
            return ChatColor.GREEN + String.valueOf(true);
        }
    }
}
