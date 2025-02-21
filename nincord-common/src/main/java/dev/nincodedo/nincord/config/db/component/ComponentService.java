package dev.nincodedo.nincord.config.db.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
@Transactional
public class ComponentService {

    private final ComponentRepository componentRepository;
    private final ComponentConfigurationRepository componentConfigurationRepository;

    public List<Component> getAllComponents() {
        return componentRepository.findAll();
    }

    public void registerComponent(String name, ComponentType componentType) {
        var componentOptional = componentRepository.findByNameAndType(name, componentType);
        if (componentOptional.isEmpty()) {
            Component component = new Component(name, componentType);
            componentRepository.save(component);
        }
    }

    @CacheEvict(allEntries = true, value = {"disabled-component", "disabled-server-component",
            "disabled-user-component"})
    public void disableComponent(String name, String serverId) {
        var component = componentRepository.findByName(name);
        var list = componentConfigurationRepository.findByComponentAndServerId(component, serverId);
        if (list.isEmpty()) {
            var componentConfiguration = new ComponentConfiguration();
            componentConfiguration.setEntityId(serverId);
            componentConfiguration.setEntityType(DiscordEntityType.SERVER);
            componentConfiguration.setDisabled(true);
            componentConfiguration.setComponent(component);
            componentConfigurationRepository.save(componentConfiguration);
        } else {
            list.forEach(componentConfiguration -> componentConfiguration.setDisabled(true));
            componentConfigurationRepository.saveAll(list);
        }
    }

    @Cacheable("disabled-component")
    public boolean isDisabled(String name, String serverId) {
        var component = componentRepository.findByName(name);
        var serverConfigurations = componentConfigurationRepository.findByComponentAndServerId(component, serverId);
        return serverConfigurations.stream()
                .filter(ComponentConfiguration::getDisabled)
                .anyMatch(componentConfiguration -> componentConfiguration.getEntityId().equals(serverId));
    }

    @Cacheable("disabled-component")
    public boolean isDisabled(String name, String serverId, String userId) {
        var component = componentRepository.findByName(name);
        boolean serverMatch = false;
        boolean userMatch = false;
        if (serverId != null) {
            var serverConfigurations = componentConfigurationRepository.findByComponentAndServerId(component, serverId);
            serverMatch = serverConfigurations.stream()
                    .filter(ComponentConfiguration::getDisabled)
                    .anyMatch(componentConfiguration -> componentConfiguration.getEntityId().equals(serverId));
        }
        if (userId != null) {
            var userConfigurations = componentConfigurationRepository.findByComponentAndUserId(component, userId);
            userMatch = userConfigurations.stream()
                    .filter(ComponentConfiguration::getDisabled)
                    .anyMatch(componentConfiguration -> componentConfiguration.getEntityId().equals(userId));
        }
        var isDisabled = serverMatch || userMatch ? "disabled" : "enabled";
        log.trace("Component {} for serverId {} and userId {} found to be {}. User: {}, Server: {}", name, serverId,
                userId, isDisabled, userMatch, serverMatch);
        return serverMatch || userMatch;
    }

    @CacheEvict(allEntries = true, value = {"disabled-component", "disabled-user-component"})
    public void setDisabledComponentsByUser(String userId, List<String> componentNames) {
        var currentUserConfiguration = componentConfigurationRepository.findByUserId(userId);
        currentUserConfiguration.forEach(currentConfig -> currentConfig.setDisabled(componentNames.contains(currentConfig.getComponent()
                .getName())));
        componentNames.forEach(componentName -> {
            var component = componentRepository.findByName(componentName);
            var currentComponents = currentUserConfiguration.stream()
                    .map(ComponentConfiguration::getComponent)
                    .toList();
            if (!currentComponents.contains(component)) {
                var addedComponentConfiguration = new ComponentConfiguration();
                addedComponentConfiguration.setComponent(component);
                addedComponentConfiguration.setDisabled(true);
                addedComponentConfiguration.setEntityType(DiscordEntityType.USER);
                addedComponentConfiguration.setEntityId(userId);
                currentUserConfiguration.add(addedComponentConfiguration);
            }
        });
        componentConfigurationRepository.saveAll(currentUserConfiguration);
    }

    public List<Component> findUserToggleableComponents() {
        var components = new ArrayList<Component>();
        var componentNames = List.of("dad", "haiku", "good-numbers");
        for (var componentName : componentNames) {
            components.add(componentRepository.findByName(componentName));
        }
        return components;
    }

    public List<ComponentConfiguration> findUserConfigurations(String userId) {
        return componentConfigurationRepository.findByUserId(userId);
    }
}
