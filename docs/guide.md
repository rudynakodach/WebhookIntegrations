# WebhookIntegrations setup guide and documentation
This guide will show you how to configure each aspect of WebhookIntegrations, including commands and permissions.

# Quick links
## [Config](configuration.md) | [Permissions](permissions.md) | [Commands](commands.md)

## First time setup
First thing you'll need to do is create a webhook on discord. To make one: 

Right-click your server > Server Settings > Integrations > Webhooks > New Webhook

This will create a new webhook in the designated channel on your server. Click on it, and press **Copy Webhook URL**.

Next, start your server enter the command `/seturl ...`, where `...` is your webhook URL which you just copied.
If done correctly, you should see a **Connected!** message appear in the channel on your Discord server.

### Multi-webhook support
WebhookIntegrations has multi-webhook support, meaning you can set a different webhook to each event. 

Each webhook url has its own ID, the default being called `main`. You can add more webhook URLs by using `/seturl ... id` where `...` is your URL and `id` is your id.

## Editing embeds
WebhookIntegrations has already some basic embeds that might suit your needs, but you can always change them however you want.

To design a new embed, you can use [this website](https://discohook.org/). Here you can create and edit embeds using GUI, and even preview your embeds before using them.

Once you're done, click **JSON Data Editor** and copy all text that appears - it's your embed's code. Now go to your server's folder, `plugins/WebhookIntegrations/messages.yml`. In this file are stored all event JSON payloads, their target etc.
Find the appropriate event for the embed you just made and replace all `messageJson` of that event to your copied code. If done correctly, you should see the new embed after reloading the plugin using `/wi reload`

## Configuring events
Events have their configuration stored in `messages.yml`. Each
in-game event that WebhookIntegrations supports is separated by its
ID:

```
onServerStart: server start event
onServerStop: server stop event
onPlayerJoin: player join event
onPlayerQuit: player quit event
onPlayerKicked: player kick event
onPlayerDeath.playerKilledByNPC: all player deaths caused by mobs, environment and non-player sources
onPlayerDeath.playerKilledByPlayer: all player deaths caused by other players
onPlayerChat: player chat event
onPlayerAdvancement: player advancement made achievement
onPlayerCountChange: player count change event, similar to join and quit events
```

Each of these events have a `target`, `canAnnounce`, `usePermission` and `messageJson` options.

`target` is the ID of the webhook where the event is sent | string

`canAnnounce` determines whether this event will be sent at all | bool

`usePermission` makes the player who triggered the event have a permission for the event to be sent (check [permissions](permissions.md)) | bool

`messageJson` is the json message of the embed

## Adding request headers
To add custom request headers, you need to add a `headers` section to `messages.yml` in the message you want to add headers to. Example:
```yml
onServerStart:
  target: "main"
  headers:
    "Content-Type": "application/json"
    "Test-Header": "example value"
    "Lorem": "ipsum"
  announce: true
  messageJson: ...
```

# Something unclear? Ask a question!
Create a support ticket [here](https://github.com/rudynakodach/WebhookIntegrations/issues/new?template=support.md), I'll get to you as fast as possible.