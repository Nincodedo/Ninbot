package com.nincraft.ninbot.components.users;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.MessageAction;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Log4j2
@Component
public class UserCommand extends AbstractCommand {

    private UserRepository userRepository;

    public UserCommand(UserRepository userRepository) {
        length = 3;
        name = "user";
        checkExactLength = false;
        this.userRepository = userRepository;
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "birthday":
                messageAction.addCorrectReaction(updateBirthday(event));
                break;
            default:
                break;
        }
        return messageAction;
    }

    private boolean updateBirthday(MessageReceivedEvent event) {
        val birthDateString = getSubcommand(event.getMessage().getContentStripped(), 3);
        val birthdayOptional = getDateWithFormat(birthDateString);
        if (birthdayOptional.isPresent()) {
            val birthday = birthdayOptional.get();
            String userId = event.getAuthor().getId();
            val optionalUser = userRepository.getFirstByUserId(userId);
            NinbotUser ninbotUser;
            if (optionalUser.isPresent()) {
                ninbotUser = optionalUser.get();
            } else {
                ninbotUser = new NinbotUser();
                ninbotUser.setUserId(userId);
                ninbotUser.setServerId(event.getGuild().getId());
            }
            ninbotUser.setBirthday(birthday);
            userRepository.save(ninbotUser);
            return true;
        } else {
            return false;
        }
    }

    private Optional<Date> getDateWithFormat(String birthdayString) {
        String birthdayFormat1 = "MM/dd/yyyy";
        String birthdayFormat2 = "MM/dd";
        try {
            return Optional.of(new SimpleDateFormat(birthdayFormat1).parse(birthdayString));
        } catch (ParseException e) {
            log.trace("Failed to parse date {} with format {}, attempting with {}", birthdayString, birthdayFormat1,
                    birthdayFormat2);
            try {
                return Optional.of(new SimpleDateFormat(birthdayFormat2).parse(birthdayString));
            } catch (ParseException ex) {
                log.error("Failed to parse date {} with format {}, nothing left to try :(", birthdayString,
                        birthdayFormat2);
                return Optional.empty();
            }
        }
    }
}
