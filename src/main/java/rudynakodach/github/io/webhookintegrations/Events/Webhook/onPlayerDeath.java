package rudynakodach.github.io.webhookintegrations.Events.Webhook;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.Modules.MessageConfiguration;
import rudynakodach.github.io.webhookintegrations.Modules.MessageType;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class onPlayerDeath implements Listener {

    JavaPlugin plugin;
    public onPlayerDeath(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {

        if(new WebhookActions(plugin).isPlayerVanished(event.getPlayer()) || new WebhookActions(plugin).isPlayerVanished(event.getEntity().getKiller())) {
            return;
        }

        String playerName = event.getEntity().getName();
        String deathMessage = PlainTextComponentSerializer.plainText().serialize(event.deathMessage() == null ? Component.empty() : Objects.requireNonNull(event.deathMessage()));

        String newLevel = String.valueOf(event.getNewLevel());
        String newExp = String.valueOf(event.getNewExp());
        String oldLevel = String.valueOf(event.getEntity().getLevel());
        String oldExp = String.valueOf(event.getEntity().getTotalExperience());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone(plugin.getConfig().getString("timezone")));

        if(event.getEntity().getKiller() != null) {
            if(!MessageConfiguration.get().canAnnounce(MessageType.PLAYER_DEATH_KILLED.getValue())) {return;}
            String killerName = event.getEntity().getKiller().getName();
            String json = MessageConfiguration.get().getMessage(MessageType.PLAYER_DEATH_KILLED.getValue());

            if(json == null) {
                return;
            }

            json = json.replace("$playersOnline$",String.valueOf(plugin.getServer().getOnlinePlayers().size()))
                .replace("$timestamp$", sdf.format(new Date()))
                .replace("$maxPlayers$",String.valueOf(plugin.getServer().getMaxPlayers()))
                    .replace("$time$", new SimpleDateFormat(
                            Objects.requireNonNullElse(
                                    plugin.getConfig().getString("date-format"),
                                    "HH:mm:ss")).format(new Date())
                    )
                .replace("$victim$",playerName)
                .replace("$killer$",killerName)
                .replace("$deathMessage$",deathMessage)
                .replace("$newLevel$",newLevel)
                .replace("$newExp$",newExp)
                .replace("$oldLevel$",oldLevel)
                .replace("$oldExp$",oldExp);

            if(plugin.getServer().getPluginManager().getPermission("PlaceholderAPI") != null) {
                json = PlaceholderAPI.setRelationalPlaceholders(event.getPlayer(), event.getEntity().getKiller(), json);
            }

            new WebhookActions(plugin).SendAsync(json);
        }
        else {
            if(!MessageConfiguration.get().canAnnounce(MessageType.PLAYER_DEATH_NPC.getValue())) {return;}
            String json = MessageConfiguration.get().getMessage(MessageType.PLAYER_DEATH_NPC.getValue());

            if(json == null) {
                return;
            }

            json = json.replace("$time$", new SimpleDateFormat(
                            Objects.requireNonNullElse(
                                    plugin.getConfig().getString("date-format"),
                                    "HH:mm:ss")).format(new Date())
                    )
                .replace("$timestamp$", sdf.format(new Date()))
                .replace("$player$",playerName)
                .replace("$deathMessage$",deathMessage)
                .replace("$newLevel$",newLevel)
                .replace("$newExp$",newExp)
                .replace("$oldLevel$",oldLevel)
                .replace("$oldExp$",oldExp);

            if(plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                json = PlaceholderAPI.setPlaceholders(event.getPlayer(), json);
            }

            new WebhookActions(plugin).SendAsync(json);
        }
    }
}
