package rudynakodach.github.io.webhookintegrations.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.WebhookIntegrations;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.logging.Logger;


public class SetWebhookURL implements CommandExecutor {

    final FileConfiguration config;
    final JavaPlugin javaPlugin;
    final Logger logger;

    public SetWebhookURL(FileConfiguration _cfg, JavaPlugin _javaPlugin) {
        config = _cfg;
        javaPlugin = _javaPlugin;
        logger = javaPlugin.getLogger();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("seturl")) {
            if (args.length == 1) {
                String newUrl = args[0];

                //checking URL validity
                if(!newUrl.contains("https://")) {
                    sender.sendMessage(ChatColor.RED + WebhookIntegrations.lang.getString(WebhookIntegrations.localeLang + ".commands.seturl.noHttps"));
                    return true;
                } else if(!newUrl.contains("discord")) {
                    sender.sendMessage(ChatColor.RED + WebhookIntegrations.lang.getString(WebhookIntegrations.localeLang + ".commands.seturl.notDiscord"));
                    return true;
                }


                sender.sendMessage(ChatColor.BLUE + WebhookIntegrations.lang.getString(WebhookIntegrations.localeLang + ".commands.seturl.verifyStart"));

                try {
                    int responseCode = getResponseCode(newUrl);

                    if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                        sender.sendMessage(ChatColor.GREEN + WebhookIntegrations.lang.getString(WebhookIntegrations.localeLang + ".commands.seturl.verifySuccess"));
                    } else {
                        sender.sendMessage(ChatColor.LIGHT_PURPLE + WebhookIntegrations.lang.getString(WebhookIntegrations.localeLang + ".commands.seturl.verifyFail"));
                        return true;
                    }
                } catch (IOException e) {
                    sender.sendMessage(ChatColor.RED + WebhookIntegrations.lang.getString(WebhookIntegrations.localeLang + ".commands.seturl.verifyFail"));
                    return true;
                }

                config.set("webhookUrl", newUrl);
                javaPlugin.saveConfig();
                sender.sendMessage(ChatColor.GREEN + WebhookIntegrations.lang.getString(WebhookIntegrations.localeLang + ".commands.seturl.newUrlSet"));
                return true;
            } else {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + WebhookIntegrations.lang.getString(WebhookIntegrations.localeLang + ".commands.seturl.commandIncorrectUsage"));
                return false;
            }
        }
        return false;
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
