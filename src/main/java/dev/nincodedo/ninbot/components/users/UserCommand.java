package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.MessageAction;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

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
            case "birthday" -> messageAction.addCorrectReaction(updateBirthday(event));
            default -> messageAction = displayHelp(event);
        }
        return messageAction;
    }

    private boolean updateBirthday(MessageReceivedEvent event) {
        val birthday = getSubcommand(event.getMessage().getContentStripped(), 3);
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
    }
}
