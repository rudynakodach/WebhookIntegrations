package rudynakodach.github.io.webhookintegrations.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import rudynakodach.github.io.webhookintegrations.WebhookActions;
import rudynakodach.github.io.webhookintegrations.WebhookIntegrations;

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SendToWebhook implements CommandExecutor {

    final FileConfiguration config;
    final JavaPlugin javaPlugin;
    final String localeName = WebhookIntegrations.localeLang;
    final FileConfiguration lang = WebhookIntegrations.lang;
    final Logger logger;

    public SendToWebhook(FileConfiguration _cfg, JavaPlugin _javaPlugin, Logger _logger) {
        config = _cfg;
        javaPlugin = _javaPlugin;
        logger = _logger;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("send")) {
            if (args.length >= 2) {
                String webhookUrl = Objects.requireNonNull(config.getString("webhookUrl"));

                if (webhookUrl.equals("")) {

                    String response = ChatColor.translateAlternateColorCodes('&', lang.getString(localeName + ".command.send.onEmptyUrl"));
                    sender.sendMessage(response);

                    return true;
                }

                boolean isEmbed = Boolean.parseBoolean(args[0]);
                String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                Player player = (Player) sender;
                String username = player.getName();

                logger.log(Level.INFO, username + " used /send with the following message: " + message);

                if (isEmbed) {

                    String json = "{" +
                            "\"embeds\": [" +
                            "{" +
                            "\"title\": \"" + username + "\"," +
                            "\"description\": \"" + message + "\"" +
                            "}" +
                            "]" +
                            "}";

                    new WebhookActions(javaPlugin).SendAsync(json);

                } else {
                    message = username + ": " + message;

                    String json = "{ \"content\": \"" + message + "\" }";

                    new WebhookActions(javaPlugin).SendAsync(json);
                }
                return true;

            } else {
                String response = ChatColor.translateAlternateColorCodes('&', lang.getString(localeName + ".commands.send.commandIncorrectUsage") + "\n" +
                        "&r&f/send &aisEmbed&7(true/false)&r&a message");
                sender.sendMessage(response);

                return true;
            }
        }
        return false;
    }
}
