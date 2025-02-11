package dev.nincodedo.nincord.config.db.component;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class ComponentService {

    private ComponentRepository componentRepository;
    private DisabledComponentsRepository disabledComponentsRepository;

    public ComponentService(ComponentRepository componentRepository,
            DisabledComponentsRepository disabledComponentsRepository) {
        this.componentRepository = componentRepository;
        this.disabledComponentsRepository = disabledComponentsRepository;
    }

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

    @CacheEvict(allEntries = true, value = {"disable-component", "disabled-server-component",
            "disabled-user-component"})
    public void disableComponent(String name, String serverId) {
        var component = componentRepository.findByName(name);
        var list = disabledComponentsRepository.findByComponentAndServerId(component, serverId);
        if (list.isEmpty()) {
            DisabledComponents disabledComponents = new DisabledComponents(serverId, component);
            disabledComponentsRepository.save(disabledComponents);
        }
    }

    private List<DisabledComponents> getDisabledComponents(Component component, String serverId) {
        return disabledComponentsRepository.findByComponentAndServerId(component, serverId);
    }

    private List<DisabledComponents> getDisabledComponents(Component component, String serverId, String userId) {
        var list = disabledComponentsRepository.findByComponentAndServerId(component, serverId);
        list.addAll(disabledComponentsRepository.findByComponentAndUserId(component, userId));
        return list;
    }

    @Cacheable("disabled-component")
    public boolean isDisabled(String name, String serverId) {
        var component = componentRepository.findByName(name);
        return !getDisabledComponents(component, serverId).isEmpty();
    }

    @Cacheable("disabled-component")
    public boolean isDisabled(String name, String serverId, String userId) {
        var component = componentRepository.findByName(name);
        return !getDisabledComponents(component, serverId, userId).isEmpty();
    }

    @CacheEvict(allEntries = true, value = {"disable-component", "disabled-server-component",
            "disabled-user-component"})
    public void enableComponent(String name, String serverId) {
        var component = componentRepository.findByName(name);
        disabledComponentsRepository.deleteAll(getDisabledComponents(component, serverId));
    }

    @CacheEvict(allEntries = true, value = {"disable-component", "disabled-server-component",
            "disabled-user-component"})
    public void enableComponent(String name, String serverId, String userId) {
        var component = componentRepository.findByName(name);
        disabledComponentsRepository.deleteAll(getDisabledComponentsByUser(component, userId));
    }

    @CacheEvict(allEntries = true, value = {"disable-component", "disabled-server-component",
            "disabled-user-component"})
    public void setDisabledComponentsByUser(String userId, List<String> componentNames) {
        disabledComponentsRepository.deleteAll(disabledComponentsRepository.findByUserId(userId));
        if (!componentNames.isEmpty()) {
            var components = componentRepository.findByNameIn(componentNames);
            disabledComponentsRepository.saveAll(components.stream()
                    .map(component -> new DisabledComponents(null, userId, component))
                    .toList());
        }
    }

    public List<DisabledComponents> getDisabledComponentsByUser(Component component, String userId) {
        return disabledComponentsRepository.findByComponentAndUserId(component, userId);
    }

    @Cacheable("disabled-server-component")
    public List<DisabledComponents> getDisabledComponents(String serverId) {
        return disabledComponentsRepository.findByServerId(serverId);
    }

    @Cacheable("disabled-user-component")
    public List<DisabledComponents> getDisabledComponentsByUser(String userId) {
        return disabledComponentsRepository.findByUserId(userId);
    }

    public List<Component> findUserToggleableComponents() {
        var components = new ArrayList<Component>();
        var componentNames = List.of("dad", "haiku", "good-numbers");
        for (var componentName : componentNames) {
            components.add(componentRepository.findByName(componentName));
        }
        return components;
    }
}
