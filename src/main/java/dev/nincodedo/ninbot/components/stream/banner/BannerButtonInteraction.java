package dev.nincodedo.ninbot.components.stream.banner;

import dev.nincodedo.ninbot.common.command.component.ButtonInteraction;
import dev.nincodedo.ninbot.common.command.component.ComponentData;
import dev.nincodedo.ninbot.common.message.ButtonInteractionCommandMessageExecutor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class BannerButtonInteraction implements ButtonInteraction {

    private final GameBannerRepository gameBannerRepository;
    private GameBannerVoteRepository gameBannerVoteRepository;

    public BannerButtonInteraction(GameBannerVoteRepository gameBannerVoteRepository,
            GameBannerRepository gameBannerRepository) {
        this.gameBannerVoteRepository = gameBannerVoteRepository;
        this.gameBannerRepository = gameBannerRepository;
    }

    @Override
    public String getName() {
        return "banner";
    }

    @Override
    public ButtonInteractionCommandMessageExecutor executeButtonPress(@NotNull ButtonInteractionEvent event,
            ComponentData componentData) {
        var messageExecutor = new ButtonInteractionCommandMessageExecutor(event);
        var gameBannerId = Long.valueOf(componentData.data());
        var userId = event.getUser().getId();
        var gameBannerOptional = gameBannerRepository.findById(gameBannerId);
        if (gameBannerOptional.isEmpty()) {
            return messageExecutor.addEphemeralMessage("Could not find that banner, did you click on an old one?");
        }
        var gameBanner = gameBannerOptional.get();
        var optionalVote = gameBanner.getVotes()
                .stream()
                .filter(gameBannerVote -> gameBannerVote.getAudit().getCreatedBy().equals(userId))
                .findFirst();
        var vote = optionalVote.orElse(new GameBannerVote());
        vote.getAudit().setCreatedModifiedBy(userId);
        var score = componentData.action().equals("good") ? 1 : -1;
        vote.setVote(score);
        messageExecutor.addEphemeralMessage("Thanks for your feedback!");
        if (vote.getGameBanner() == null) {
            vote.setGameBanner(gameBanner);
        }
        gameBannerVoteRepository.save(vote);
        return messageExecutor;
    }
}
