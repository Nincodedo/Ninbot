# Automatic Actions

Automatic Actions are various Ninbot modules that need little to no configuration. They can be disabled with
the [Component command](../commands/index.md#component).

* [Temporary Voice Channels](#temporary-voice-channels)
* [Pathogen Game](#pathogen-game)
* [New Emote Announcements](#new-emote-announcements)

## Temporary Voice Channels

Component name: `voice-channel-manager`

Voice Channels with ➕ in the beginning of their name will automatically create voice channels when a person joins and
move them into it. This channel is deleted as soon as it is empty. The user that created the channel will also have mute
and move permissions in the channel as well as delete and rename. The channel will automatically be deleted as soon as
it is empty.

## Pathogen Game

Component name: `pathogen`

The pathogen game requires a role name "infected" on the server for it to begin. Each day, a list of 15 common words are
the secret words of the day. If a user says one of the secret words of the day, the 5 surrounding messages have a chance
of becoming infected or healed, depending on which week it is. Ninbot will add an emoji on the message that got
infected/healed. This doesn't do anything, but it's funny to guess which words are the secret words of the day.

## New Emote Announcements

Component name: `emote-added-announcement`

New emotes added to the server can be automatically announced in a specific channel if configured. Add the configuration
`emoteAnnouncementChannelId` with the channel id as the value to have Ninbot automatically announce.
The component can also be disabled with the following name.
