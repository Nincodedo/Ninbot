package dev.nincodedo.ninbot.components.users;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
class UserService {
    private UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void updateBirthday(String userId, String guildId, String birthday) {
        var optionalUser = userRepository.getFirstByUserId(userId);
        NinbotUser ninbotUser;
        if (optionalUser.isPresent()) {
            ninbotUser = optionalUser.get();
        } else {
            ninbotUser = new NinbotUser();
            ninbotUser.setUserId(userId);
            ninbotUser.setServerId(guildId);
        }
        ninbotUser.setBirthday(birthday);
        userRepository.save(ninbotUser);
    }

    public void toggleBirthdayAnnouncement(String userId) {
        userRepository.getFirstByUserId(userId)
                .ifPresent(ninbotUser -> ninbotUser.setAnnounceBirthday(!ninbotUser.getAnnounceBirthday()));
    }
}
