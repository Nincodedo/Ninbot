# Configurations

Server admins have a few options to configure Ninbot with the Config command. Here are the available configurations and their defaults, if it applies.

## Announcement Channel
Name: announcementChannel

Usage: Channel ID for Event announcements.

## Dadbot Blacklist
Name: dadbotChannelBlacklist

Usage: Channel IDs for blacklisting Dadbot reactions.

## Streaming Announce Users
Name: streamingAnnounceUsers

Usage: List of users that will be announced when the stream. This is managed by the Twitch command and does not need to be used by the Config command.

## Streaming Announce Channel
Name: streamingAnnounceChannel

Usage: Channel ID for Twitch announcements.

## Role Blacklist
Name: roleBlacklist

Usage: Blacklist of role names you don't want to show up in the subscribe/unsubscribe command.

## Streaming Role
Name: streamingRole

Usage: Role ID for role you want to be assigned when someone starts streaming.

## Conversation Channels
Name: conversationChannels

Usage: Channel IDs that Ninbot can have normal conversations in. Managed by the Conversation command.

## Leaderboard Name
Name: leaderboardName

Usage: Name of the server wide leaderboard for the Leaderboard, Win, and Lose commands.

## Topic Change Channel
Name: topicChangeChannel

Usage: Channel IDs that Ninbot will announce when the topic is changed.

## Server Locale
Name: serverLocal

Usage: Changes Ninbot's language server wide.

Possible options: de, en, es, fr, it, pt, ru

Default: en

## Server Timezone
Name: serverTimezone

Usage: Sets the server's timezone, used for event announcements. Should be formatted as GMT+1, GMT-1, or just GMT.

Default: GMT-6