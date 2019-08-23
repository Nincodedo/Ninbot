package com.nincraft.ninbot.components.ac;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class VillagerManager {

    private VillagerRepository villagerRepository;

    public VillagerManager(VillagerRepository villagerRepository) {
        this.villagerRepository = villagerRepository;
    }


    boolean sellTurnips(Villager villager, int amountSelling, int turnipPrice) {
        if (amountSelling > 0 && villager.getTurnipsOwned() >= amountSelling) {
            villager.setTurnipsOwned(villager.getTurnipsOwned() - amountSelling);
            villager.setBellsTotal(villager.getBellsTotal() + amountSelling * turnipPrice);
            villagerRepository.save(villager);
            return true;
        }
        return false;
    }

    Optional<Villager> findByDiscordId(String discordId) {
        return villagerRepository.findByDiscordId(discordId);
    }

    public void save(Villager villager) {
        villagerRepository.save(villager);
    }

    boolean buyTurnips(Villager villager, int amountBuying, int currentPrice) {
        if (amountBuying > 0 && villager.getBellsTotal() >= currentPrice * amountBuying) {
            villager.setTurnipsOwned(villager.getTurnipsOwned() + amountBuying);
            villager.setBellsTotal(villager.getBellsTotal() - amountBuying * currentPrice);
            villagerRepository.save(villager);
            return true;
        }
        return false;
    }

    List<Villager> getTopTenBellVillagers() {
        return villagerRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Villager::getBellsTotal).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
}
