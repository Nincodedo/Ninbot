package com.nincraft.ninbot.components.config.component;

import com.nincraft.ninbot.NinbotTest;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ComponentServiceTest extends NinbotTest {

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
        val list = componentService.getAllComponents();
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

    @Test
    void disableComponent() {
        
    }

    @Test
    void isDisabled() {
    }

    @Test
    void enableComponent() {
    }

    @Test
    void getDisabledComponents() {
    }
}