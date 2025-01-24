# WebhookIntegrations commands
### [Back to guide](guide.md)

## General commands

`/seturl <url> <?id>`: sets a webhooks url to provided id
- Permission: `webookintegrations.seturl`
- Params:
- - `url`: Required, url to your webhook
- - `id`: Optional, the id of your webhook for multi-webhook usage, defaults to `main`
---
`/wi`: base command
- Permission: `webhookintegrations.wi`
---
`/wi reload`: reload all configs
- Permission: `webhookintegrations.reload`
---
`/wi config reset`: resets all configs to default
- Permission: `webhookintegrations.config.reset`
- #### ⚠️ Note: this is a dangerous command that nobody should have access to ⚠️
---
`/wi config setvalue <path> <val>`: sets a config value
- Permission: `webhookintegrations.config.setvalue`
- #### ⚠️ Note: this is a dangerous command that nobody should have access to ⚠️
- Params:
- - `<path>`: Required, the path separated with dots (`.`) to the value
- - `<val>`: Required, the new value to set 
---
`/wi config savebackup <?name>`: saves a config backup
- Permission: `webhookintegrations.config.savebackup`
- Params:
- - `<name>`: Optional, the name for the backup, defaults to current time
---
`/wi config loadbackup <name>`: loads a backup
- Permission: `webhookintegrations.loadbackup`
- Params:
- - `<name>`: Required, the name of the backup to load
---
`/wi disable`: disables sending events to webhooks completely until turned back on
- Permission: `webhookintegrations.disable`
---
`/wi enable`: enables sending events to webhooks
- Permission: `webhookintegrations.enable`
---
`/wi help`: displays the link to this user guide
- Permission: `webhookintegrations.help`
---
`/wi update`: updates the plugin
- Permission: `webhookintegrations.update`
---
`/wi setlanguage`: changes the plugin language
- Permission: `webhookintegrations.setlanguage`
---
`/wi template send <name> <?target> <?args>`
- Permission: `webhookintegrations.templates.send`
- Params:
- - `<name>`: Required, the id of the template
- - `<?target>`: Optional, the webhook id to send the template to, defaults to `main`
- - `<?args>`: Optional, the arguments for your template in the format `--arg "text"` (will replace `%arg%` with `text` in the message)
