# Reactions

Ninbot has some automation actions it will take based on various types of messages.

* [Dadbot](#dadbot)
* [Haiku](#haiku)
* [Reaction Emojis](#reaction-emojis)

## Dadbot

Hi, I'm Dad. Dadbot detects messages that start with a variation of "I'm" and responds with the appropriate dad joke.

## Haiku

If Ninbot detects that a message was a Haiku, it will respond with it in Haiku form.

## Reaction Emojis

See [the reactions JSON.](/ninbot-app/src/main/resources/responses.json) Ninbot will respond with any reaction that can
be made with only emoji letters (words only made with at most one of each letter).
