package dev.nincodedo.nincord.util;

import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class ClassScanner {
    public static <T> List<Class<T>> findClassesThatExtend(Class<T> clazz) {
        BeanDefinitionRegistry definitionRegistry = new SimpleBeanDefinitionRegistry();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(definitionRegistry);
        TypeFilter typeFilter = new AssignableTypeFilter(clazz);
        scanner.resetFilters(false);
        scanner.setIncludeAnnotationConfig(false);
        scanner.addIncludeFilter(typeFilter);
        scanner.scan("dev.nincodedo.nincord");
        var parserClassNameList = definitionRegistry.getBeanDefinitionNames();
        return Arrays.stream(parserClassNameList)
                .map(definitionRegistry::getBeanDefinition)
                .map(BeanDefinition::getBeanClassName)
                .map(classStringName -> {
                    try {
                        return Class.forName(classStringName);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(aClass -> (Class<T>) aClass).toList();
    }
}
