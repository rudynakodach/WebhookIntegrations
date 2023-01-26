package rudynakodach.github.io.webhookintegrations.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import rudynakodach.github.io.webhookintegrations.PlayerEventListener;

import java.util.logging.Logger;


public class SetWebhookURL implements CommandExecutor {

    final PlayerEventListener playerEventListener;
    final FileConfiguration config;
    final JavaPlugin javaPlugin;

    final Logger logger;

    public SetWebhookURL(PlayerEventListener _pel, FileConfiguration _cfg, JavaPlugin _javaPlugin, Logger _logger) {
        playerEventListener = _pel;
        config = _cfg;
        javaPlugin = _javaPlugin;
        logger = _logger;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("seturl")) {
            if (args.length == 1) {
                String newUrl = args[0];

                playerEventListener.webhookUrl = newUrl;
                config.set("webhookUrl", newUrl);
                javaPlugin.saveConfig();

                sender.sendMessage("New webhook url has been set!");
                return true;
            } else {
                sender.sendMessage("Incorrect usage.");
                return false;
            }
        }
        return false;
    }
}
