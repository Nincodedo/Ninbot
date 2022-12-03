![Ninbot Logo](docs/images/ninbot-github-social.png)

# Ninbot ![Master Branch Build](https://github.com/Nincodedo/Ninbot/workflows/Master%20Build/Deploy/badge.svg?branch=master) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=dev.nincodedo%3Aninbot&metric=alert_status)](https://sonarcloud.io/dashboard?id=dev.nincodedo%3Aninbot) [![Uptime](https://status.nincodedo.dev/api/badge/1/uptime/720?label=Uptime&labelSuffix=&color=blue)](https://status.nincodedo.dev)

Ninbot is a Discord bot with various silly commands and actions, such as dabbing. Ninbot is built for OCW Discord
using [JDA.](https://github.com/DV8FromTheWorld/JDA)

## Features

Dab on your friends.

![Dab command example light](docs/images/dab-slash-command-example1-light.png#gh-light-mode-only)
![Dab command example dark](docs/images/dab-slash-command-example1-dark.png#gh-dark-mode-only)

Experience the father you never had but wish you did.

![Dadbot reaction example light](docs/images/dadbot-example1-light.png#gh-light-mode-only)
![Dadbot reaction example dark](docs/images/dadbot-example1-dark.png#gh-dark-mode-only)

## Want to use Ninbot on your Discord server?

[Click here](https://discordapp.com/oauth2/authorize?client_id=204484879554052096&scope=bot&permissions=823520464) to
add Ninbot to your server!

- [Check the documentation for a list of features and help with commands](https://ninbot.nincodedo.dev/)
- [Create an issue if you think you've found a bug](https://github.com/Nincodedo/Ninbot/issues/new/choose)
- [Ask a question in GitHub Discussions if you need additional help](https://github.com/Nincodedo/Ninbot/discussions/new?category=q-a)

## Want to help develop Ninbot?

Make sure to read our [contributing expectations](CONTRIBUTING.md) before making any changes.

### Development Goals

#### Use the reaction JSON framework

Prioritize the reaction framework when possible. Adding a target phrase and a response to
the [responses.json](ninbot-app/src/main/resources/responses.json) is sometimes all you need to do. Responses done
through this will react with emojis automatically if it is possible. This also helps with the next development goal...

#### Minimize Chat Clutter

Prioritize reacting with emojis when possible. Sending chat messages should only be done if absolutely necessary (comedy
is sometimes necessary). Commonly used Emojis for Ninbot can be found in
the [common Emojis class](nincord-common/src/main/java/dev/nincodedo/nincord/Emojis.java).

#### Updated Documentation

Changes and additions to a command or action that would affect the end user should be documented. The latest version of
Ninbot is the only supported version, so the documentation needs to be up-to-date.
