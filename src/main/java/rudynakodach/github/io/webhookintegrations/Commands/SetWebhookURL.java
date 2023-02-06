package rudynakodach.github.io.webhookintegrations.Commands;

import okhttp3.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import rudynakodach.github.io.webhookintegrations.WebhookIntegrations;

import java.io.IOException;
import java.util.logging.Logger;


public class SetWebhookURL implements CommandExecutor {

    final FileConfiguration config;
    final JavaPlugin javaPlugin;
    final FileConfiguration lang = WebhookIntegrations.lang;
    final String localeName = WebhookIntegrations.localeLang;
    final Logger logger;

    public SetWebhookURL(FileConfiguration _cfg, JavaPlugin _javaPlugin) {
        config = _cfg;
        javaPlugin = _javaPlugin;
        logger = javaPlugin.getLogger();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("seturl")) {
            if (args.length == 1) {
                String newUrl = args[0];

                //checking URL validity
                if(!newUrl.contains("discord")) {

                }
                else if(!newUrl.contains("https://")) {

                }

                sender.sendMessage(ChatColor.BLUE + lang.getString(localeName + ".commands.seturl.verifyStart"));

                try {
                    Response response = getResponse(newUrl);

                    if(response.isSuccessful()) {
                        sender.sendMessage(ChatColor.GREEN + lang.getString(localeName + ".commands.seturl.verifySuccess"));
                    } else {
                        sender.sendMessage(ChatColor.LIGHT_PURPLE + lang.getString(localeName + ".commands.seturl.verifyFail"));
                        return true;
                    }
                } catch (IOException e) {
                    sender.sendMessage(ChatColor.RED + lang.getString(localeName + ".commands.seturl.verifyFail"));
                    return true;
                }

                config.set("webhookUrl", newUrl);
                javaPlugin.saveConfig();
                sender.sendMessage(ChatColor.GREEN + lang.getString(localeName + ".commands.seturl.newUrlSet"));
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + lang.getString(localeName + ".commands.seturl.commandIncorrectUsage"));
                return false;
            }
        }
        return false;
    }

    private Response getResponse(String url) throws IOException {
        String json = "{\"content\": \"**Connected!**\"}";

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.get("application/json");
        RequestBody body = RequestBody.create(json,mediaType);

        Request req = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return client.newCall(req).execute();
    }
}
