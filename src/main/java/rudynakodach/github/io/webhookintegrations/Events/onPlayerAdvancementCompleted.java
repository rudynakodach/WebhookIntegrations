package rudynakodach.github.io.webhookintegrations.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class onPlayerAdvancementCompleted implements Listener {
    JavaPlugin plugin;
    AdvancementMap advancementMap = new AdvancementMap();
    public onPlayerAdvancementCompleted(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAdvancementMade(PlayerAdvancementDoneEvent event) {
        if (!plugin.getConfig().getBoolean("onPlayerAdvancementComplete.announce")) {
            return;
        }

        String advKey = event.getAdvancement().getKey().getKey();
        String advancement = advancementMap.map.get(advKey); //ITS WORKING...

        String json = plugin.getConfig().getString("onPlayerAdvancementComplete.messageJson");

        json = json.replace("%playersOnline%",String.valueOf(plugin.getServer().getOnlinePlayers().size()));
        json = json.replace("%advancement%", advancement);
        json = json.replace("%player%", event.getPlayer().getName());
        json = json.replace("%uuid%",event.getPlayer().getUniqueId().toString());
        json = json.replace("%time%", new SimpleDateFormat("HH:mm:ss").format(new Date()));

        new WebhookActions(plugin).Send(json);
    }
}