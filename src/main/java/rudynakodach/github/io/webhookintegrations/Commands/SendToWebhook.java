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
import java.util.Objects;
import java.util.logging.Level;


public class SendToWebhook implements CommandExecutor {

    final FileConfiguration config;
    final JavaPlugin javaPlugin;
    final LanguageConfiguration language;
    public SendToWebhook(FileConfiguration cfg, JavaPlugin javaPlugin) {
        config = cfg;
        this.javaPlugin = javaPlugin;
        this.language = LanguageConfiguration.get();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("send")) {
            if (args.length >= 2) {
                String webhookUrl = Objects.requireNonNull(config.getString("webhookUrl"));

                if (webhookUrl.equals("")) {
                    String response = ChatColor.translateAlternateColorCodes('&',
                            language.getString("commands.send.onEmptyUrl"));
                    sender.sendMessage(response);
                    return true;
                }

                boolean isEmbed = Boolean.parseBoolean(args[0]);
                String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                String username = sender instanceof ConsoleCommandSender ? "CONSOLE" : sender.getName();

                javaPlugin.getLogger().log(Level.INFO, username + " used /send with the following message: " + message);

                if (isEmbed) {
                    String json = "{\"embeds\": [ {\"title\": \"" + username + "\",\"description\": \"" + message + "\"}]}";

                    new WebhookActions(javaPlugin).SendAsync(json);
                } else {
                    message = username + ": " + message;
                    String json = "{ \"content\": \"" + message + "\" }";

                    new WebhookActions(javaPlugin).SendAsync(json);
                }
                return true;

            } else {
                String response = ChatColor.translateAlternateColorCodes('&',
                        language.getString("commands.send.commandIncorrectUsage") + "\n" +
                        "&r&f/send &aisEmbed&7(true/false)&r&a message");
                sender.sendMessage(response);

                return true;
            }
        }
        return false;
    }
}
