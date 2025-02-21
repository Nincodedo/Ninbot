package dev.nincodedo.ninbot.components.users;

import dev.nincodedo.nincord.config.db.component.ComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ComponentService componentService;

    public NinbotUser getUserById(String userId) {
        NinbotUser user;
        var optionalUser = userRepository.getByUserId(userId);
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            user = new NinbotUser(userId);
            userRepository.saveAndFlush(user);
        }
        user.setUserSettings(componentService.findUserConfigurations(userId));
        return user;
    }

    public void setDisableComponentsByUser(String userId, List<String> disabledComponentNames) {
        componentService.setDisabledComponentsByUser(userId, disabledComponentNames);
    }
}
