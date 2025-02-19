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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import rudynakodach.github.io.webhookintegrations.Modules.LanguageConfiguration;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.util.Arrays;
import java.util.logging.Level;


public class SendToWebhook implements CommandExecutor {

    final FileConfiguration config;
    final JavaPlugin plugin;
    final LanguageConfiguration language;
    public SendToWebhook(FileConfiguration cfg, JavaPlugin plugin) {
        config = cfg;
        this.plugin = plugin;
        this.language = LanguageConfiguration.get();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("send")) {
            if (args.length >= 2) {
                String target = args[0];

                boolean isEmbed = Boolean.parseBoolean(args[1]);
                String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

                String username = sender instanceof ConsoleCommandSender ? "CONSOLE" : sender.getName();

                plugin.getLogger().log(Level.INFO, username + " used /send with the following message: " + message);

                if (isEmbed) {
                    String json = "{\"embeds\": [ {\"title\": \"" + username + "\",\"description\": \"" + message + "\"}]}";

                    new WebhookActions(plugin, target, json).SendAsync();
                } else {
                    message = username + ": " + message;
                    String json = "{ \"content\": \"" + message + "\" }";

                    new WebhookActions(plugin, target, json).SendAsync();
                }
                return true;

            } else {
                String response = ChatColor.translateAlternateColorCodes('&',
                        language.getLocalizedString("commands.send.commandIncorrectUsage") + "\n" +
                        "&r&f/send &aisEmbed&7(true/false)&r&a message");
                sender.sendMessage(response);

                return true;
            }
        }
        return false;
    }
}
