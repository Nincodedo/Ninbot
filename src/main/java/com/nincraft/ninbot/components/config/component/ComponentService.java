package com.nincraft.ninbot.components.config.component;

import lombok.val;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
        val componentOptional = componentRepository.findByNameAndType(name, componentType);
        if (!componentOptional.isPresent()) {
            Component component = new Component(name, componentType);
            componentRepository.save(component);
        }
    }

    void disableComponent(String name, String serverId) {
        val component = componentRepository.findByName(name);
        val list = disabledComponentsRepository.findByComponentAndServerId(component, serverId);
        if (list.isEmpty()) {
            DisabledComponents disabledComponents = new DisabledComponents(serverId, component);
            disabledComponentsRepository.save(disabledComponents);
        }
    }

    private List<DisabledComponents> getDisabledComponents(Component component, String serverId) {
        return disabledComponentsRepository.findByComponentAndServerId(component, serverId);
    }

    public boolean isDisabled(String name, String serverId) {
        val component = componentRepository.findByName(name);
        return !getDisabledComponents(component, serverId).isEmpty();
    }

    void enableComponent(String name, String serverId) {
        val component = componentRepository.findByName(name);
        getDisabledComponents(component, serverId).forEach(disabledComponents ->
                disabledComponentsRepository.delete(disabledComponents));
    }

    public List<DisabledComponents> getDisabledComponents(String serverId) {
        return disabledComponentsRepository.findByServerId(serverId);
    }
}
