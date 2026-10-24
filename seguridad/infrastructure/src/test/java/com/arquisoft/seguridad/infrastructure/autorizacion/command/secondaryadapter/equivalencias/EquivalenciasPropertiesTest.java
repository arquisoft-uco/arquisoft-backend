package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias;

import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias.EquivalenciasProperties.Entrada;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EquivalenciasPropertiesTest {

    @Test
    void debeFallar_cuandoHayDuplicados() {
        // Arrange
        var entradas = List.of(new Entrada("GET", "/api/x", "r1"), new Entrada("get", "/api/x", "r2"));

        // Act / Assert
        assertThatThrownBy(() -> new EquivalenciasProperties(entradas)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void debeFallar_cuandoPathNoEmpiezaPorBarraORolVacio() {
        // Act / Assert
        assertThatThrownBy(() -> new Entrada("GET", "api/x", "r")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new Entrada("GET", "/api/x", " ")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void debeCargarElYamlReal_cuandoSeEnlazaLaTabla() throws Exception {
        // Arrange
        var fuentes = new MutablePropertySources();
        new YamlPropertySourceLoader().load("equivalencias", new ClassPathResource("autorizacion-equivalencias.yml"))
                .forEach(fuentes::addLast);
        var binder = new Binder(ConfigurationPropertySources.from(fuentes));

        // Act
        var propiedades = binder.bind("security.authorization.role-table", EquivalenciasProperties.class).get();

        // Assert
        assertThat(propiedades.entries()).hasSizeGreaterThan(100);
    }
}
