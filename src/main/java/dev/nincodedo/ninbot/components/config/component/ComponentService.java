package dev.nincodedo.ninbot.components.config.component;

import lombok.val;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
        if (componentOptional.isEmpty()) {
            Component component = new Component(name, componentType);
            componentRepository.save(component);
        }
    }

    @CacheEvict(allEntries = true, value = {"disable-component", "disabled-server-component"})
    public void disableComponent(String name, String serverId) {
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

    @Cacheable("disabled-component")
    public boolean isDisabled(String name, String serverId) {
        val component = componentRepository.findByName(name);
        return !getDisabledComponents(component, serverId).isEmpty();
    }

    void enableComponent(String name, String serverId) {
        val component = componentRepository.findByName(name);
        disabledComponentsRepository.deleteAll(getDisabledComponents(component, serverId));
    }

    @Cacheable("disabled-server-component")
    public List<DisabledComponents> getDisabledComponents(String serverId) {
        return disabledComponentsRepository.findByServerId(serverId);
    }
}
