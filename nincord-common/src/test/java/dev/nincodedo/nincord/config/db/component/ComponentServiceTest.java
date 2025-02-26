package dev.nincodedo.nincord.config.db.component;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComponentServiceTest {

    @Mock
    ComponentRepository componentRepository;

    @Mock
    ComponentConfigurationRepository componentConfigurationRepository;

    @InjectMocks
    ComponentService componentService;

    @Test
    void getAllComponents() {
        Component component = new Component("test", ComponentType.COMMAND);
        List<Component> componentList = new ArrayList<>();
        componentList.add(component);

        when(componentRepository.findAll()).thenReturn(componentList);

        var list = componentService.getAllComponents();
        assertThat(list).isNotEmpty();
    }

    @Test
    void registerNewComponent() {
        String name = "test";
        ComponentType componentType = ComponentType.COMMAND;

        when(componentRepository.findByNameAndType(name, componentType)).thenReturn(Optional.empty());

        componentService.registerComponent(name, componentType);
        verify(componentRepository, times(1)).save(Mockito.any(Component.class));
    }

    @Test
    void registerExistingComponent() {
        String name = "test";
        ComponentType componentType = ComponentType.COMMAND;
        Component component = new Component(name, componentType);

        when(componentRepository.findByNameAndType(name, componentType)).thenReturn(Optional.of(component));

        componentService.registerComponent(name, componentType);
        verify(componentRepository, Mockito.never()).save(Mockito.any(Component.class));
    }
}
