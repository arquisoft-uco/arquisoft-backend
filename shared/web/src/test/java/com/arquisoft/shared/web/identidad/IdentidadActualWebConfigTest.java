package com.arquisoft.shared.web.identidad;

import org.junit.jupiter.api.Test;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class IdentidadActualWebConfigTest {

    @Test
    void debeRegistrarElResolver_cuandoSeConfiguranLosArgumentos() {
        // Arrange
        var resolvers = new ArrayList<HandlerMethodArgumentResolver>();

        // Act
        new IdentidadActualWebConfig(() -> null).addArgumentResolvers(resolvers);

        // Assert
        assertThat(resolvers).singleElement().isInstanceOf(IdentidadActualArgumentResolver.class);
    }
}
