# Automatic Actions
Automatic Actions are various Ninbot modules that need little to no configuration. They can be disabled with the [Component command](../commands/index.md#component). 
## Temporary Voice Channels
Voice Channels with ➕ in the beginning of their name will automatically create voice channels when a person joins them
and move them into it. This channel is deleted as soon as it is empty. The user that created the channel will also have
mute and move permissions in the channel as well as delete and rename. The channel will automatically be deleted as soon
as it is empty.

Component name:
    
    voice-channel-manager
    
## Pathogen Game
The pathogen game requires a role name "infected" on the server for it to begin. Each day, a list of 30 common words each day. If a user says one of the secret words of the day, the 5 surrounding messages have a chance of becoming infected. Ninbot will add an emoji on the message that got infected. This doesn't do anything, but it's funny to guess which words are the secret words of the day.

Component name:

    pathogen