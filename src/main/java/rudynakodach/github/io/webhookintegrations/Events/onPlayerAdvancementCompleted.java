package rudynakodach.github.io.webhookintegrations.Events;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class onPlayerAdvancementCompleted implements Listener {
    JavaPlugin plugin;
    YamlConfiguration advancementConfig;
    public onPlayerAdvancementCompleted(JavaPlugin plugin) {
        this.plugin = plugin;
        File langFile = new File(plugin.getDataFolder(),"advancements.yml");
        this.advancementConfig = YamlConfiguration.loadConfiguration(langFile);
    }
    @EventHandler
    public void onAdvancementMade(PlayerAdvancementDoneEvent event) {
        if (event.getPlayer() != null) {
            if (!plugin.getConfig().getBoolean("onPlayerAdvancementComplete.announce")) {
                return;
            }
            String advKey = event.getAdvancement().getKey().getKey();

            if (!advancementConfig.contains(advKey)) {
                plugin.getLogger().log(Level.WARNING, "Unrecognized advancement key: " + advKey + "" + "\nMessage send canceled. If you believe this is an error report this on the GitHub repo here: https://github.com/rudynakodach/WebhookIntegrations/issues/new or add it in advancements.yml file.");
                return;
            }
            String advancement = advancementConfig.getString(advKey);

            String json = plugin.getConfig().getString("onPlayerAdvancementComplete.messageJson");

            json = json.replace("%playersOnline%", String.valueOf(plugin.getServer().getOnlinePlayers().size()));
            json = json.replace("%advancement%", advancement);
            json = json.replace("%player%", event.getPlayer().getName());
            json = json.replace("%uuid%", event.getPlayer().getUniqueId().toString());
            json = json.replace("%time%", new SimpleDateFormat("HH:mm:ss").format(new Date()));

            new WebhookActions(plugin).Send(json);
        }
    }
}