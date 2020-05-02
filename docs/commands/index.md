# Commands
All commands are prefixed with @Ninbot.
  * [User Commands](#user-commands)
    * [8 ball](#8-ball)
    * [Countdown](#countdown)
    * [Dab](#dab)
    * [Define](#define)
    * [Events](#events)
    * [Help](#help)
    * [Hugemoji](#hugemoji)
    * [Info](#info)
    * [Leaderboard](#leaderboard)
    * [List](#list)
    * [Lose/Win](#losewin)
    * [Pathogen](#pathogen)
    * [Poll](#poll)
    * [Roll](#roll)
    * [Simulate](#simulate)
    * [Stats](#stats)
    * [Subscribe/Unsubscribe](#subscribeunsubscribe)
    * [Trivia](#trivia)
    * [Turnips](#turnips)
    * [Twitch](#twitch)
  * [Mods Commands](#mod-commands)
    * [Conversation](#conversation)
    * [Topic Change Announcement](#topic-change-announcement)
  * [Admin Commands](#admin-commands)
    * [Component](#component)
    * [Config](#config)
    * [Leaderboard](#admin-leaderboard)

## User Commands

### 8 Ball
Asks the magic 8 ball a question.

Usage:

    @Ninbot 8ball

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
    
### Hugemoji
Shows a huge version of the emoji used

Usage:

    @Ninbot hugemoji :thumbsup:
    
### Info
Shows info about Ninbot such as uptime, and the most recent commit.

Usage:

    @Ninbot info
    
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
         
### Pathogen
Displays the user's current infection level.

Usage:
    
    @Ninbot pathogen

### Poll
Creates a new poll. You can have a maximum of 10 answers in a single poll. 

Example: @Ninbot poll Who stole the cookies from the cookie jar? "You, me, then who?" 20

This would create a poll with three options, and it would close after 20 minutes.

Usage:

    @Ninbot poll Poll question "Answer 1, Answer 2, Answer 3" PollLengthInMinutes
    
### Roll
Rolls one 20 sided die by default. Can also take in [Dice Notation](https://en.wikipedia.org/wiki/Dice_notation).

Usage:

    @Ninbot roll
    
    @Ninbot roll 2d20
    
### Simulate
Simulates messages from another user.

Usage:

    @Ninbot simulate @Username

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
    
### Turnips
Buy/Sell turnips like in Animal Crossing. Any turnips will rot after a week so be sure to sell them before Sunday.
#### Subcommands
##### Join
Join the stalk market system. You need to do this once to create your villager profile. Villagers start with 2000 Bells.

Usage:

    @Ninbot turnips join
    
##### Buy
Buy an amount of turnips. You can only buy turnips on Sundays.

Usage:

    @Ninbot turnips buy NUMBER
    
##### Sell
Sell an amount of turnips. You cannot sell turnips on Sundays.

Usage:

    @Ninbot turnips sell NUMBER
    
##### Price
Shows the current price of turnips. On Sundays, shows how much you can buy turnips for.
Every other day it shows how much you can sell them for. Each individual server has it's own prices every day.

Usage:

    @Ninbot turnips price

##### Wallet
Shows your current inventory, including your Bells and turnips.

Usage:

    @Ninbot turnips wallet
    
### Twitch
Twitch related commands.
#### Subcommands
##### Announce
Toggles your going live announcement.

Usage:

    @Ninbot twitch announce
    
    
## Mod Commands
### Conversation
Toggles the Ninbot conversation module in the current channel. It is off by default.

Usage:

    @Ninbot conversation
    

### Topic Change Announcement
Toggles topic change announcements in the current channel.

Usage:

    @Ninbot topic-change
    
## Admin Commands

### Config

### Component
List/Enable/Disable individual components of Ninbot

Usage:

    @Ninbot component list
    
    @Ninbot component disable COMPONENTNAME
    
    @Ninbot component enable COMPONENTNAME


### Admin Leaderboard