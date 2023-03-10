package rudynakodach.github.io.webhookintegrations.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import rudynakodach.github.io.webhookintegrations.WebhookActions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class onPlayerDeath implements Listener {

    JavaPlugin plugin;
    public onPlayerDeath(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String playerName = event.getEntity().getName();
        String deathMessage = event.getDeathMessage();

        String newLevel = String.valueOf(event.getNewLevel());
        String newExp = String.valueOf(event.getNewExp());
        String oldLevel = String.valueOf(event.getEntity().getLevel());
        String oldExp = String.valueOf((int) event.getEntity().getExp());

        if(event.getEntity().getKiller() != null) {
            if(!plugin.getConfig().getBoolean("onPlayerDeath.playerKilledByPlayer.announce")) {return;}
            String killerName = event.getEntity().getKiller().getName();
            String json = plugin.getConfig().getString("onPlayerDeath.playerKilledByPlayer.messageJson");

            json = json.replace("%playersOnline%",String.valueOf(plugin.getServer().getOnlinePlayers().size()));
            json = json.replace("%maxPlayers%",String.valueOf(plugin.getServer().getMaxPlayers()));
            json = json.replace("%time%",time);
            json = json.replace("%player%",playerName);
            json = json.replace("%killer%",killerName);
            json = json.replace("%deathMessage%",deathMessage);
            json = json.replace("%newLevel%",newLevel);
            json = json.replace("%newExp%",newExp);
            json = json.replace("%oldLevel%",oldLevel);
            json = json.replace("%oldExp%",oldExp);

            new WebhookActions(plugin).Send(json);
        }
        else {
            if(!plugin.getConfig().getBoolean("onPlayerDeath.playerKilledByNPC.announce")) {return;}
            String json = plugin.getConfig().getString("onPlayerDeath.playerKilledByNPC.messageJson");

            json = json.replace("%time%",time);
            json = json.replace("%player%",playerName);
            json = json.replace("%deathMessage%",deathMessage);
            json = json.replace("%newLevel%",newLevel);
            json = json.replace("%newExp%",newExp);
            json = json.replace("%oldLevel%",oldLevel);
            json = json.replace("%oldExp%",oldExp);

            new WebhookActions(plugin).Send(json);
        }
    }
}
