package rudynakodach.github.io.webhookintegrations;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import okhttp3.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerEventListener implements Listener {

    //config logic
    String webhookUrl;

    final Boolean isAnnouncingPlayerJoin;
    final Boolean isAnnouncingPlayerQuit;
    final Boolean isAnnouncingPlayerKills;
    final Boolean isAnnouncingPlayerDeaths;

    final Boolean isAnnouncingPlayerAdvancements;
    final Boolean isAnnouncingPlayerChatMessages;

    final Logger logger;
    final FileConfiguration config;
    JavaPlugin javaPlugin;

    public PlayerEventListener(Logger _log, FileConfiguration _config, JavaPlugin _plgn) {
        logger = _log;
        config = _config;

        javaPlugin = _plgn;

        webhookUrl = config.getString("webhookUrl");

        isAnnouncingPlayerJoin = config.getBoolean("announcePlayerJoin");
        isAnnouncingPlayerQuit = config.getBoolean("announcePlayerQuit");
        isAnnouncingPlayerKills = config.getBoolean("announcePlayerKills");
        isAnnouncingPlayerDeaths = config.getBoolean("announcePlayerDeaths");

        isAnnouncingPlayerAdvancements = config.getBoolean("announceAdvancements");
        isAnnouncingPlayerChatMessages = config.getBoolean("announceChatMessages");
    }

    //done
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if(!isAnnouncingPlayerJoin) {return;}

        String eventMessage = config.getString("onPlayerJoinEventMessage");
        String playerName = event.getPlayer().getName();

        int onJoinEmbedColor = config.getInt("onPlayerJoinEventEmbedColor");

        //date
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(new Date());

        eventMessage = eventMessage.replace("%time%",time);
        eventMessage = eventMessage.replace("%player%",playerName);

        String json = "{" +
                "\"embeds\": [" +
                "{" +
                "\"color\": " + onJoinEmbedColor + "," +
                "\"description\": \"" + eventMessage + "\"" +
                "}" +
                "]" +
                "}";

        Send(json);
    }

    //done
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        if(!isAnnouncingPlayerQuit) {return;}

        int embedColor = config.getInt("onPlayerQuitEventEmbedColor");

        Player player = event.getPlayer();
        String eventMessage = config.getString("onPlayerQuitEventMessage");
        eventMessage = eventMessage.replace("%time%", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        eventMessage = eventMessage.replace("%player%", player.getName());


        String json = "{" +
                "\"embeds\": [" +
                "{" +
                "\"color\": " + embedColor + "," +
                "\"description\": \"" + eventMessage + "\"" +
                "}" +
                "]" +
                "}";

        Send(json);
    }

    //done
    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {

        if(!isAnnouncingPlayerChatMessages) {return;}

        String eventMessage = config.getString("chatEventMessage");
        int webhookEmbedColor = config.getInt("chatEventEmbedColor");

        String playerName = event.getPlayer().getName();
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

        eventMessage = eventMessage.replace("%time%", time);
        eventMessage = eventMessage.replace("%player%", playerName);
        eventMessage = eventMessage.replace("%message%", PlainTextComponentSerializer.plainText().serialize(event.message()));

        String json = "{" +
                "\"embeds\": [" +
                "{" +
                "\"color\": 16776960," +
                "\"description\": \"" + eventMessage + "\"" +
                "}" +
                "]" +
                "}";

        Send(json);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        String playerName = event.getPlayer().getName();
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

        //noinspection DataFlowIssue
        if (event.getEntity().getKiller() instanceof Player && isAnnouncingPlayerKills) {
            String killerName = event.getEntity().getKiller().getName();


            ItemStack weapon = event.getEntity().getKiller().getInventory().getItemInMainHand();

            //item name known
            if (!weapon.getItemMeta().displayName().equals("")) {

                String eventMessage = config.getString("playerKilledByWeaponEventMessage");
                int webhookEmbedColor = config.getInt("playerKilledByWeaponEmbedColor");

                eventMessage = eventMessage.replace("%time%",time);
                eventMessage = eventMessage.replace("%player%", playerName);
                eventMessage = eventMessage.replace("%killer%",killerName);
                eventMessage = eventMessage.replace("%weapon%",PlainTextComponentSerializer.plainText().serialize(weapon.getItemMeta().displayName()));

                String json = "{" +
                        "\"embeds\": [" +
                        "{" +
                        "\"color\": " + webhookEmbedColor + "," +
                        "\"description\": \"" + eventMessage + "," +
                        "}" +
                        "]" +
                        "}";
                Send(json);

            } else {
                //item name unknown
                if(isAnnouncingPlayerKills) {

                    String eventMessage = config.getString("playerKilledEventMessage");
                    int webhookEmbedColor = config.getInt("playerKilledEventEmbedColor");

                    eventMessage = eventMessage.replace("%time%",time);
                    eventMessage = eventMessage.replace("%player%", playerName);

                    String json = "{" +
                            "\"embeds\": [" +
                            "{" +
                            "\"color\": " + webhookEmbedColor + "," +
                            "\"description\": \"" + eventMessage + "\"" +
                            "}" +
                            "]" +
                            "}";

                    Send(json);
                }
            }
        }
        else {
            if(isAnnouncingPlayerDeaths) {

                String eventMessage = config.getString("playerDeathEventMessage");
                int webhookEmbedColor = config.getInt("playerDeathEventEmbedColor");

                eventMessage = eventMessage.replace("%time%",time);
                eventMessage = eventMessage.replace("%player%", playerName);

                String json = "{" +
                        "\"embeds\": [" +
                        "{" +
                        "\"color\": " + webhookEmbedColor + "," +
                        "\"description\": \"" + eventMessage + "\"" +
                        "}" +
                        "]" +
                        "}";

                Send(json);
            }
        }
    }


    //done
    @EventHandler
    public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
        if(!isAnnouncingPlayerAdvancements) {return;}

        String advancementName = PlainTextComponentSerializer.plainText().serialize(event.getAdvancement().displayName());

        if (advancementName.contains(":") || advancementName.contains("minecraft")) {
            return;
        }

        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String playerName = event.getPlayer().getName();

        int webhookEmbedColor = config.getInt("playerAdvancementEventEmbedColor");
        String message = config.getString("playerAdvancementEvent");


        message = message.replace("%time%", time);
        message = message.replace("%player%", playerName);
        message = message.replace("%advancement%", advancementName);

        String json = "{" +
                "\"embeds\": [" +
                "{" +
                "\"color\": \"" + webhookEmbedColor + "\"," +
                "\"description\": \"" + message + "\"" +
                "}" +
                "]" +
                "}";

        Send(json);
    }


    public void Send(String json) {

        if(webhookUrl.equals(""))  {
            logger.log(Level.WARNING, "Attempted to perform a POST request to an empty webhook url!");
            return;
        }

        new BukkitRunnable() {
            public void run() {
                OkHttpClient client = new OkHttpClient();

                MediaType mediaType = MediaType.get("application/json");
                RequestBody body = RequestBody.create(json, mediaType);
                Request request = new Request.Builder()
                        .url(webhookUrl)
                        .post(body)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        logger.log(Level.WARNING, "Failed to send eventMessage to Discord webhook: " + response.body().string());
                    }
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage());
                }
            }
        }.runTaskAsynchronously(javaPlugin);
    }
}
