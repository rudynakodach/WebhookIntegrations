package rudynakodach.github.io.webhookintegrations.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rudynakodach.github.io.webhookintegrations.WebhookIntegrations;

import java.util.ArrayList;
import java.util.List;

public class ConfigActions implements CommandExecutor, TabCompleter {
    JavaPlugin plugin;
    public ConfigActions(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
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
                }
            }
            else {
                if(player.hasPermission("webhookintegrations.wi")) {
                    String text = "/wi\n\treset\n\t\t&cconfirm&r\n\treload";
                    text = text.replace("\n", "  ");
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            text));
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        if(command.getName().equalsIgnoreCase("wi")) {

            if (args.length == 0) {
                suggestions.add("reset");
                suggestions.add("reload");
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
}
