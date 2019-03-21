# Commands
All commands are prefixed with @Ninbot.
  * [User Commands](#user-commands)
    * [Countdown](#countdown)
    * [Dab](#dab)
    * [Define](#define)
    * [Events](#events)
    * [Help](#help)
    * [Leaderboard](#leaderboard)
    * [List](#list)
    * [Lose/Win](#losewin)
    * [Poll](#poll)
    * [Roll](#roll)
    * [Stats](#stats)
    * [Subscribe/Unsubscribe](#subscribeunsubscribe)
    * [Trivia](#trivia)
    * [Twitch](#twitch)
  * [Admin Commands](#admin-commands)
    * [Config](#config)
    * [Leaderboard](#admin-leaderboard)

## User Commands

### Countdown
Setup a countdown that will be announced every day leading up to the event.

Usage:

    @Ninbot countdown YYYY-MM-DD CountdownName
    
### Dab
Adds all dab emojis to the last message of the user named.

Usage:

    @Ninbot dab @Username 
    
### Define
Defines a word.

Usage:

    @Ninbot define Word
    
### Events
List or schedule events.
#### Subcommands
##### List
Shows all future events.

Usage:

    @Ninbot events list
    
##### Plan
Schedules an event. Event times are in GMT -6 and formatted 2019-01-31T12:00:00-06:00 for January 31st 2019 at noon.

Usage:

    @Ninbot events plan "Event Name" StartTime GameName
    
### Help
DMs you with a list of commands available to you. You can also use help after any main command to get more information.

Usage:

    @Ninbot help
    
### Leaderboard
Shows the server's leaderboard.

Usage:

    @Ninbot leaderboard
    
### List
Shows the available role subscriptions.

Usage:

    @Ninbot list
    
### Lose/Win
Report a win or a loss against another user for the leaderboard.

Usage:

    @Ninbot win @Username
    
    @Ninbot lose @Username
    
Aliases: won, beat, smashed, owned
         lost, loss
### Poll
Creates a new poll. You can have a maximum of 10 answers in a single poll. 

Example: @Ninbot poll Who stole the cookies from the cookie jar? "You, me, then who?" 20

This would create a poll with three options and it would close after 20 minutes.

Usage:

    @Ninbot poll Poll question "Answer 1, Answer 2, Answer 3" PollLengthInMinutes
    
### Roll
Rolls one 20 sided die by default. Can also take in [Dice Notation](https://en.wikipedia.org/wiki/Dice_notation).

Usage:

    @Ninbot roll
    
    @Ninbot roll 2d20
    
### Stats
Shows various stats on Ninbot.

Usage:

    @Ninbot stats
    
### Subscribe/Unsubscribe
Subscribes/unsubscribes you to a role for events.

Usage:

    @Ninbot subscribe RoleName
    
    @Ninbot unsubscribe RoleName
    
### Trivia
Starts/stops trivia.
#### Subcommands
##### Start
Starts trivia. Optionally add a category ID to only have trivia questions from that category. 

Usage:

    @Ninbot trivia start [ID]
    
##### Stop
Stops trivia.

Usage:

    @Ninbot trivia stop
    
##### Categories
Lists available trivia categories.

Usage:

    @Ninbot trivia categories
    
##### Score
Shows your current score.

Usage:

    @Ninbot trivia score
    
##### Leaderboard
Shows the top 5 trivia players.

Usage:

    @Ninbot trivia leaderboard
    
### Twitch
Twitch related commands.
#### Subcommands
##### Announce
Toggles your going live announcement.

Usage:

    @Ninbot twitch announce
    
## Admin Commands
### Config
### Admin Leaderboard