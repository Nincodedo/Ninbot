package com.nincraft.ninbot.components.ac;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
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

    private TurnipPricesManager turnipPricesManager;
    private VillagerManager villagerManager;

    public TurnipCommand(TurnipPricesManager turnipPricesManager, VillagerManager villagerManager) {
        name = "turnips";
        length = 3;
        checkExactLength = false;
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
            case "prices":
                commandResult.addChannelAction(listTurnipPrices(event));
                break;
            case "wallet":
                commandResult.addChannelAction(getVillagerBells(event));
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
            embedBuilder.setTitle(String.format(resourceBundle.getString("command.turnips.wallet.title"),
                    event.getMember().getEffectiveName()));
            embedBuilder.addField(resourceBundle.getString("command.turnips.wallet.bells.title"),
                    String.format("%,d", villager.getBellsTotal()), false);
            embedBuilder.addField(resourceBundle.getString("command.turnips.wallet.whiteturnips.title"),
                    String.format("%,d", villager.getTurnipsOwned()), false);
            return new MessageBuilder(embedBuilder).build();
        }
        return new MessageBuilder().append(resourceBundle.getString("command.turnips.wallet.novillager")).build();
    }

    private boolean joinTurnipEvent(MessageReceivedEvent event) {
        if (!villagerManager.findByDiscordId(event.getAuthor().getId()).isPresent()) {
            Villager villager = new Villager();
            villager.setDiscordId(event.getAuthor().getId());
            villager.setDiscordServerId(event.getGuild().getId());
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
            embedBuilder.setTitle(resourceBundle.getString("command.turnips.list.title"));
            embedBuilder.addField(resourceBundle.getString("command.turnips.wallet.whiteturnips.title"),
                    String.format(resourceBundle.getString("command.turnips.list.bellsper"), currentPrice), false);
            return new MessageBuilder(embedBuilder).build();
        }
        return null;
    }

    private long getSeed(long serverId) {
        return turnipPricesManager.findAll().get(0).getSeed() + serverId;
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
                        resourceBundle.getString("command.turnips.list.sunday.joan"), turnipPrice));
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
            int turnipPrice = getCurrentPrice(getSeed(event.getGuild().getIdLong()));
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
            int currentPrice = turnipPricesManager.getSundayTurnipPrices(seed);
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
