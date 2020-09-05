package com.nincraft.ninbot.components.ac.turnips;

import com.nincraft.ninbot.components.ac.Villager;
import com.nincraft.ninbot.components.ac.VillagerManager;
import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.MessageAction;
import lombok.Setter;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class TurnipCommand extends AbstractCommand {

    private static final String SELLING_BUYING_COMMAND_MAX = "max";
    private TurnipPricesManager turnipPricesManager;
    private VillagerManager villagerManager;
    @Setter
    private Clock clock;

    public TurnipCommand(TurnipPricesManager turnipPricesManager, VillagerManager villagerManager) {
        name = "turnips";
        length = 3;
        checkExactLength = false;
        this.clock = Clock.systemDefaultZone();
        this.turnipPricesManager = turnipPricesManager;
        this.villagerManager = villagerManager;
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        val message = event.getMessage().getContentStripped();
        switch (getSubcommand(message)) {
            case "join" -> messageAction.addCorrectReaction(joinTurnipEvent(event));
            case "buy" -> messageAction.addCorrectReaction(buyTurnips(event));
            case "sell" -> messageAction.addCorrectReaction(sellTurnips(event));
            case "price", "prices" -> messageAction.addChannelAction(listTurnipPrices(event));
            case "leaderboard" -> messageAction.addChannelAction(getLeaderboard(event));
            case "inv", "inventory", "wallet" -> messageAction.addChannelAction(getVillagerInventory(event));
            default -> messageAction = displayHelp(event);
        }

        return messageAction;
    }

    private Message getVillagerInventory(MessageReceivedEvent event) {
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

    private Message getLeaderboard(MessageReceivedEvent event) {
        val villagerList = villagerManager.getTopTenBellVillagers();
        if (villagerList.isEmpty()) {
            return null;
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(String.format(resourceBundle.getString("command.turnips.leaderboard.title"),
                villagerList.size()));
        for (val villager : villagerList) {
            val user = event.getJDA().getShardManager().getUserById(villager.getDiscordId());
            if (user != null) {
                embedBuilder.addField(user.getName(), villager.getBellsTotalFormatted(), false);
            }
        }
        return new MessageBuilder(embedBuilder).build();
    }

    private boolean joinTurnipEvent(MessageReceivedEvent event) {
        if (villagerManager.findByDiscordId(event.getAuthor().getId()).isEmpty()) {
            Villager villager = new Villager();
            villager.setDiscordId(event.getAuthor().getId());
            villager.setDiscordServerId(event.getGuild().getId());
            villagerManager.save(villager);
            return true;
        }
        return false;
    }

    private Message listTurnipPrices(MessageReceivedEvent event) {
        if (LocalDate.now(clock).getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
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
        return turnipPricesManager.findNewest().getSeed() + serverId;
    }

    private int getCurrentPrice(long seed) {
        val turnipPattern = turnipPricesManager.getTurnipPattern(seed);
        val priceList = turnipPricesManager.getTurnipPricesList(turnipPattern, seed);
        int priceIndex = ((LocalDate.now(clock).getDayOfWeek().getValue() - 1) * 2) + (
                LocalDateTime.now(clock).getHour() >= 12 ? 1 : 0);
        return priceList.get(priceIndex);
    }

    private void listSundayTurnipSellingPrices(MessageReceivedEvent event) {
        int turnipPrice = turnipPricesManager.getSundayTurnipPrices(
                getSeed(event.getGuild().getIdLong()));
        val webhookOptional = webhookHelper.getWebhookByName(event.getGuild(), event.getTextChannel(), "joan");
        if (webhookOptional.isPresent()) {
            val webhook = webhookOptional.get();
            webhookHelper.sendMessage(String.format(
                    resourceBundle.getString("command.turnips.list.sunday.joan"), turnipPrice), webhook.getUrl());
        }
    }

    private boolean sellTurnips(MessageReceivedEvent event) {
        if (LocalDate.now(clock).getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            return false;
        }
        val villagerOptional = villagerManager.findByDiscordId(event.getAuthor().getId());
        if (villagerOptional.isPresent()) {
            val villager = villagerOptional.get();
            int turnipPrice = getCurrentPrice(getSeed(event.getGuild().getIdLong()));
            val message = event.getMessage().getContentStripped();
            int selling = 0;
            if (getCommandLength(message) > 3) {
                String sellAmount = message.split("\\s+")[3];
                if (StringUtils.isNumeric(sellAmount)) {
                    selling = Integer.parseInt(sellAmount);
                } else if (SELLING_BUYING_COMMAND_MAX.equals(sellAmount)) {
                    selling = villager.getTurnipsOwned();
                }
            }
            return villagerManager.sellTurnips(villager, selling, turnipPrice);
        } else {
            return false;
        }
    }

    private boolean buyTurnips(MessageReceivedEvent event) {
        if (!LocalDate.now(clock).getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            return false;
        }
        val villagerOptional = villagerManager.findByDiscordId(event.getAuthor().getId());
        if (villagerOptional.isPresent()) {
            val villager = villagerOptional.get();
            long seed = getSeed(event.getGuild().getIdLong());
            int currentPrice = turnipPricesManager.getSundayTurnipPrices(seed);
            val message = event.getMessage().getContentStripped();
            int amountBuying = getAmountBuying(villager, currentPrice, message);
            return villagerManager.buyTurnips(villager, amountBuying, currentPrice);
        }
        return false;
    }

    private int getAmountBuying(Villager villager, int currentPrice, String message) {
        int amountBuying = 0;
        if (getCommandLength(message) > 3) {
            String buyAmount = message.split("\\s+")[3];
            if (StringUtils.isNumeric(buyAmount)) {
                amountBuying = Integer.parseInt(message.split("\\s+")[3]);
            } else if (SELLING_BUYING_COMMAND_MAX.equals(buyAmount)) {
                amountBuying = villager.getBellsTotal() / currentPrice / 10 * 10;
            }
        }
        return amountBuying;
    }
}
