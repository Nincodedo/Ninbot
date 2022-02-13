package dev.nincodedo.ninbot.components.config.component;

import dev.nincodedo.ninbot.NinbotRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {NinbotRunner.class})
@TestPropertySource(locations = {"classpath:application.properties", "classpath:ninbot.properties"})
class ComponentServiceTest {

    @Mock
    ComponentRepository componentRepository;

    @Mock
    DisabledComponentsRepository componentSettingRepository;

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
        verify(componentRepository, times(0)).save(Mockito.any(Component.class));
    }
}