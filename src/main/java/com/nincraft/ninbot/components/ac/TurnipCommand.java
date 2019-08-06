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
    private TurnipPricesManager turnipPricesManager;
    private VillagerManager villagerManager;

    public TurnipCommand(TurnipPricesRepository turnipPricesRepository, TurnipPricesManager turnipPricesManager,
            VillagerManager villagerManager) {
        name = "turnips";
        length = 3;
        checkExactLength = false;
        this.turnipPricesRepository = turnipPricesRepository;
        this.turnipPricesManager = turnipPricesManager;
        this.villagerManager = villagerManager;
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
            case "wallet":
                commandResult.addChannelAction(getVillagerBells(event));
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

    private Message getVillagerBells(MessageReceivedEvent event) {
        val villagerOptional = villagerManager.findByDiscordId(event.getAuthor().getId());
        if (villagerOptional.isPresent()) {
            val villager = villagerOptional.get();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(String.format("%s's Wallet", event.getMember().getEffectiveName()));
            embedBuilder.addField("Bells", Integer.toString(villager.getBellsTotal()), false);
            embedBuilder.addField("White Turnips", Integer.toString(villager.getTurnipsOwned()), false);
            return new MessageBuilder(embedBuilder).build();
        }
        return new MessageBuilder().append("No villager found! Use \"@Ninbot turnips join\" to join the town!").build();
    }

    private boolean joinTurnipEvent(MessageReceivedEvent event) {
        if (!villagerManager.findByDiscordId(event.getAuthor().getId()).isPresent()) {
            Villager villager = new Villager();
            villager.setDiscordId(event.getAuthor().getId());
            villagerManager.save(villager);
            return true;
        }
        return false;
    }

    private Message listTurnipPrices(MessageReceivedEvent event) {
        if (LocalDate.now().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            listSundayTurnipSellingPrices(event);
        } else {
            long seed = getSeed(event.getGuild().getIdLong());
            int currentPrice = getCurrentPrice(seed);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Current Turnip Selling Prices");
            embedBuilder.addField("White Turnips", String.format("%d bells per turnip", currentPrice), false);
            return new MessageBuilder(embedBuilder).build();
        }
        return null;
    }

    private long getSeed(long serverId) {
        return turnipPricesRepository.findAll().get(0).getSeed() + serverId;
    }

    private int getCurrentPrice(long seed) {
        val priceList = turnipPricesManager.getTurnipPricesList(TurnipPattern.getRandomTurnipPattern(seed), seed);
        int priceIndex = ((LocalDate.now().getDayOfWeek().getValue() - 1) * 2) + (
                LocalDateTime.now().getHour() >= 12 ? 1 : 0);
        return priceList.get(priceIndex);
    }

    private void listSundayTurnipSellingPrices(MessageReceivedEvent event) {
        int turnipPrice = turnipPricesManager.getSundayTurnipPrices(
                getSeed(event.getGuild().getIdLong()));
        event.getGuild().retrieveWebhooks().queue(webhooks -> webhooks.forEach(webhook -> {
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
        }));
    }

    private boolean sellTurnips(MessageReceivedEvent event) {
        if (LocalDate.now().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            return false;
        }
        val villagerOptional = villagerManager.findByDiscordId(event.getAuthor().getId());
        if (villagerOptional.isPresent()) {
            val villager = villagerOptional.get();
            int turnipPrice = turnipPricesManager.getSundayTurnipPrices(getSeed(event.getGuild().getIdLong()));
            val message = event.getMessage().getContentStripped();
            int selling = 0;
            if (getCommandLength(message) > 3) {
                selling = Integer.parseInt(message.split("\\s+")[3]);
            }
            return villagerManager.sellTurnips(villager, selling, turnipPrice);
        } else {
            return false;
        }
    }

    private boolean buyTurnips(MessageReceivedEvent event) {
        if (!LocalDate.now().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            return false;
        }
        val villagerOptional = villagerManager.findByDiscordId(event.getAuthor().getId());
        if (villagerOptional.isPresent()) {
            val villager = villagerOptional.get();
            long seed = getSeed(event.getGuild().getIdLong());
            int currentPrice = getCurrentPrice(seed);
            val message = event.getMessage().getContentStripped();
            int amountBuying = 0;
            if (getCommandLength(message) > 3) {
                amountBuying = Integer.parseInt(message.split("\\s+")[3]);
            }
            return villagerManager.buyTurnips(villager, amountBuying, currentPrice);
        }
        return false;
    }
}
