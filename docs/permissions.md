# WebhookIntegrations permissions
### [Back to guide](guide.md)

## Command permissions

### General commands

`webhookintegrations.config.reset`: /wi config reset

`webhookintegrations.reload`: /wi reload

`webhookintegrations.analyze`: /wi analyze

`webhookintegrations.update`: /wi update

`webhookintegrations.enable`: /wi enable

`webhookintegrations.disable`: /wi disable

`webhookintegrations.setlaunguage`: /wi setlanguage

`webhookintegrations.config.setvalue`: /wi config setvalue

`webhookintegrations.config.savebackup`: /wi config savebackup

`webhookintegrations.config.loadbackup`: /wi config loadbackup

### Templates

`webhookintegrations.templates.send.any`: access to all templates

`webhookintegrations.templates.send.<id>`: access to template with id of `<id>`

## Event permissions
### ⚠️ Note: for those permissions to be required, you need to set `usePermissions` to `true` in the event's section in `messages.yml` file!!!

`webhookintegrations.events.all`: allows for sending all events

`webhookintegrations.events.onPlayerJoin`: player join event 

`webhookintegrations.events.onPlayerQuit`: player quit event

`webhookintegrations.events.onPlayerKicked`: player kicked event

`webhookintegrations.events.onPlayerDeath.playerKilledByNPC`: player death event

`webhookintegrations.events.onPlayerDeath.playerKilledByPlayer`: player killed event

`webhookintegrations.events.onPlayerChat`: player chat event

`webhookintegrations.events.onPlayerAdvancement`: player advancement made event
