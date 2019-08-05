package com.nincraft.ninbot.components.ac;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.RolePermission;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class TurnipCommand extends AbstractCommand {

    private TurnipPricesRepository turnipPricesRepository;
    private VillagerRepository villagerRepository;
    private TurnipPricesManager turnipPricesManager;

    public TurnipCommand(TurnipPricesRepository turnipPricesRepository, VillagerRepository villagerRepository,
            TurnipPricesManager turnipPricesManager) {
        name = "turnips";
        length = 3;
        checkExactLength = false;
        this.turnipPricesRepository = turnipPricesRepository;
        this.villagerRepository = villagerRepository;
        this.turnipPricesManager = turnipPricesManager;
    }


    @Override
    protected CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "join":
                commandResult.addCorrectReaction(joinTurnipEvent(event));
                break;
            case "buy":
                commandResult.addCorrectReaction(buyTurnips(event));
                break;
            case "sell":
                commandResult.addCorrectReaction(sellTurnips(event));
                break;
            case "price":
                commandResult.addChannelAction(listTurnipPrices(event));
                break;
            case "admin":
                if (userHasPermission(event.getGuild(), event.getAuthor(), RolePermission.ADMIN)) {
                    turnipPricesManager.generateNewWeek();
                    commandResult.addSuccessfulReaction();
                } else {
                    commandResult.addUnsuccessfulReaction();
                }
                break;
            default:
                commandResult = displayHelp(event);
                break;
        }

        return commandResult;
    }

    private boolean joinTurnipEvent(MessageReceivedEvent event) {
        if (!villagerRepository.findByDiscordId(event.getAuthor().getId()).isPresent()) {
            Villager villager = new Villager();
            villager.setDiscordId(event.getAuthor().getId());
            villagerRepository.save(villager);
            return true;
        }
        return false;
    }

    private Message listTurnipPrices(MessageReceivedEvent event) {
        if (LocalDate.now().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            val turnipPrices = turnipPricesRepository.findAll();
            int turnipPrice = turnipPricesManager.getSundayTurnipPrices(
                    turnipPrices.get(0).getSeed() + event.getGuild().getIdLong());
            event.getGuild().retrieveWebhooks().queue(webhooks -> {
                webhooks.forEach(webhook -> {
                    if (webhook.getName().equalsIgnoreCase("joan")) {
                        WebhookClientBuilder builder = new WebhookClientBuilder(webhook.getUrl());
                        WebhookClient client = builder.build();
                        WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();
                        messageBuilder.append(String.format(
                                "Well, hello there, kiddo. I've been selling turnips here on Sunday "
                                        + "morns for 'bout 60 years now. Maybe even more! So anyway, today the asking"
                                        + " price "
                                        + "is %d Bells per turnip. I sell them in bunches of 10.", turnipPrice));
                        client.send(messageBuilder.build());
                        client.close();
                    }
                });
            });
        } else {
            val turnipPrices = turnipPricesRepository.findAll().get(0);
            long seed = turnipPrices.getSeed() + event.getGuild().getIdLong();
            val priceList = turnipPricesManager.getTurnipPricesList(TurnipPattern.getRandomTurnipPattern(seed), seed);
            int priceIndex = ((LocalDate.now().getDayOfWeek().getValue() - 1) * 2) - (
                    LocalDateTime.now().getHour() >= 12 ? 1 : 0);
            int currentPrice = priceList.get(priceIndex);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Current Turnip Selling Prices");
            embedBuilder.addField("White Turnips", String.format("%d bells per turnip", currentPrice), false);
            return new MessageBuilder(embedBuilder).build();

        }
        return null;
    }

    private boolean sellTurnips(MessageReceivedEvent event) {
        if (LocalDate.now().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {

            return false;
        }
        return false;
    }

    private boolean buyTurnips(MessageReceivedEvent event) {
        if (!LocalDate.now().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            return false;
        }


        return true;
    }
}
