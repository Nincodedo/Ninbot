![Ninbot Logo](docs/images/ninbot-github-social.png)
# Ninbot ![Master Branch Build](https://github.com/Nincodedo/Ninbot/workflows/Master%20Build/Deploy/badge.svg?branch=master) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.nincraft%3Aninbot&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.nincraft%3Aninbot)
Ninbot is a Discord bot with various silly commands and actions, such as dabbing. Ninbot is built for OCW Discord using [JDA.](https://github.com/DV8FromTheWorld/JDA)

## Want to use Ninbot on your Discord server?
[Click here](https://discordapp.com/oauth2/authorize?client_id=204484879554052096&scope=bot&permissions=823520464) to add Ninbot to your server!

- [Check the documentation for a list of features and help with commands](https://ninbot.nincodedo.dev/)
- [Create an issue if you think you've found a bug](https://github.com/Nincodedo/Ninbot/issues/new/choose)
- [Join the support Discord server if you need additional help](https://discord.gg/hdw9NhJ)

## Want to help develop Ninbot?

Make sure to read our [contributing expectations](CONTRIBUTING.md) before making any changes.

### Development Goals

#### Use the reaction JSON framework

Prioritize the reaction framework when possible. Adding a target phrase and a response to the [responses.json](src/main/resources/responses.json) is sometimes all you need to do. Responses done through this will react with emojis automatically if its possible. This also helps with the next development goal...

#### Minimize Chat Clutter

Prioritize reacting with emojis when possible. Sending chat messages should only be done if absolutely neccessary (comedy is sometimes neccessary). Commonly used Emojis for Ninbot can be found in the [common Emojis class](src/main/java/dev/nincodedo/ninbot/components/common/Emojis.java).

|  Action|Emoji  |
|--------|-------|
|Success |✅     |
|Failure |❌     |
|Unknown |❔      |

#### Updated Documentation

Changes and additions to a command or action that would affect the end user should be documented. The latest version of Ninbot is the only supported version, so the documentation needs to be up to date.
