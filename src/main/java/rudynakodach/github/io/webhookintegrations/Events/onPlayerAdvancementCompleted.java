package rudynakodach.github.io.webhookintegrations.Events;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class onPlayerAdvancementCompleted implements Listener {
    JavaPlugin plugin;

    public onPlayerAdvancementCompleted(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAdvancementMade(PlayerAdvancementDoneEvent event) {
        if (!plugin.getConfig().getBoolean("onPlayerAdvancementComplete.announce")) {
            return;
        }
        String advancement = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(event.getAdvancement().getDisplay()).title());

        //if it is a recipe
        if (advancement.contains(":")) {
            return;
        }
        String json = plugin.getConfig().getString("onPlayerAdvancementComplete.messageJson");
        assert json != null;

        json = json.replace("%advancement%", advancement);
        json = json.replace("%player%", event.getPlayer().getName());
        json = json.replace("%time%", new SimpleDateFormat("HH:mm:ss").format(new Date()));

        new WebhookActions(plugin).Send(json);
    }
}