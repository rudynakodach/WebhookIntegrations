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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import rudynakodach.github.io.webhookintegrations.Modules.LanguageConfiguration;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.logging.Logger;


public class SetWebhookURL implements CommandExecutor {

    final FileConfiguration config;
    final JavaPlugin plugin;
    final Logger logger;
    final LanguageConfiguration language;

    public SetWebhookURL(JavaPlugin plugin) {
        config = plugin.getConfig();
        this.plugin = plugin;
        logger = this.plugin.getLogger();

        this.language = LanguageConfiguration.get();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("seturl")) {
            return true;
        }

        if (args.length >= 1) {
            String newUrl = args[0].trim();
            String target = "main";
            if(args.length >= 2) {
                target = args[1].trim();
            }

            //checking URL validity
            if (!newUrl.startsWith("https://")) {
                sender.sendMessage(ChatColor.RED + language.getLocalizedString("commands.seturl.noHttps"));
                return true;
            } else if (!newUrl.contains("discord")) {
                sender.sendMessage(ChatColor.RED + language.getLocalizedString("commands.seturl.notDiscord"));
                return true;
            }

            sender.sendMessage(ChatColor.BLUE + language.getLocalizedString("commands.seturl.verifyStart"));

            try {
                int responseCode = getResponseCode(newUrl);

                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                    sender.sendMessage(ChatColor.GREEN + language.getLocalizedString("commands.seturl.verifySuccess"));
                } else {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + language.getLocalizedString("commands.seturl.verifyFail"));
                    return true;
                }
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + language.getLocalizedString("commands.seturl.verifyFail"));
                return true;
            }

            config.set("webhooks.%s".formatted(target), newUrl);
            plugin.saveConfig();
            sender.sendMessage(ChatColor.GREEN + language.getLocalizedString("commands.seturl.newUrlSet"));
            return true;
        } else {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + language.getLocalizedString("commands.seturl.commandIncorrectUsage"));
            return false;
        }
    }

    private int getResponseCode(String target) throws IOException {
        String json = "{\"content\": \"**Connected!**\"}";
        URL url = new URL(target);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(json.getBytes());
        outputStream.flush();
        outputStream.close();

        return connection.getResponseCode();
    }
}
