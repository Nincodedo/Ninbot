package dev.nincodedo.ninbot.components.poll;

import dev.nincodedo.ninbot.common.message.MessageAction;
import dev.nincodedo.ninbot.common.command.AbstractCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class PollCommand extends AbstractCommand {

    private PollScheduler pollScheduler;

    public PollCommand(PollScheduler pollScheduler) {
        name = "poll";
        length = 4;
        checkExactLength = false;
        this.pollScheduler = pollScheduler;
    }

    //TODO implement SlashCommand
    @Override
    protected MessageAction executeCommand(PrivateMessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);

        return messageAction;
    }
/*
    @Override
    public MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        if (isCommandLengthCorrect(event.getMessage().getContentStripped())) {
            Poll poll = parsePollMessage(event.getMessage(), event.getMember());
            poll.setResourceBundle(resourceBundle);
            poll.setLocaleString(LocaleService.getLocale(event).toString());
            if (!poll.getChoices().isEmpty() && poll.getChoices().size() <= Constants.POLL_CHOICE_LIMIT) {
                event.getChannel()
                        .sendMessage(poll.build())
                        .queue(message -> {
                            poll.setMessageId(message.getId());
                            pollScheduler.addPoll(poll, event.getJDA().getShardManager());
                        });
            } else {
                messageAction.addUnsuccessfulReaction();
            }
        }
        return messageAction;
    }*/

    Poll parsePollMessage(Message message, Member member) {
        Poll poll = new Poll();
        poll.setChannelId(message.getTextChannel().getId());
        poll.setServerId(message.getGuild().getId());
        poll.setUserAvatarUrl(member.getUser().getAvatarUrl());
        poll.setUserName(member.getEffectiveName());
        var pollMessage = message.getContentStripped().substring("@Ninbot poll ".length());
        poll.setChoices(new ArrayList<>());
        if (pollMessage.contains("\"")) {
            poll.setTitle(pollMessage.substring(0, pollMessage.indexOf("\"")).trim());
            var pollOptions = pollMessage.substring(
                    pollMessage.indexOf("\"") + 1, pollMessage.lastIndexOf("\"")).replace("\"", "");
            poll.setChoices(Arrays.stream(pollOptions.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList()));
            //If a + is included in the choices, allowed for other users to add their own choices
            poll.setUserChoicesAllowed((poll.getChoices().remove("+")));
            var timeString = pollMessage.substring(pollMessage.lastIndexOf("\"") + 1).trim();
            if (StringUtils.isNotBlank(timeString)) {
                poll.setEndDateTime(LocalDateTime.now().plus(Long.parseLong(timeString), ChronoUnit.MINUTES));
            } else {
                poll.setEndDateTime(LocalDateTime.now().plus(5, ChronoUnit.MINUTES));
            }
        }
        return poll;
    }

}
