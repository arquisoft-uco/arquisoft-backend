package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias;

import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias.SoloAutenticacionProperties.Entrada;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SoloAutenticacionPropertiesTest {

    @Test
    void debeFallar_cuandoHayDuplicados() {
        // Arrange
        var entradas = List.of(new Entrada("GET", "/api/x"), new Entrada("get", "/api/x"));

        // Act & Assert
        assertThatThrownBy(() -> new SoloAutenticacionProperties(entradas))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void debeFallar_cuandoElPathNoEmpiezaPorBarraOElMetodoEstaVacio() {
        // Act & Assert
        assertThatThrownBy(() -> new Entrada("GET", "api/x")).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new Entrada(" ", "/api/x")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void debeCargarElYamlReal_cuandoSeEnlazaLaSeccion() throws Exception {
        // Arrange
        var fuentes = new MutablePropertySources();
        new YamlPropertySourceLoader().load("equivalencias", new ClassPathResource("autorizacion-equivalencias.yml"))
                .forEach(fuentes::addLast);
        var binder = new Binder(ConfigurationPropertySources.from(fuentes));

        // Act
        var propiedades = binder.bind("security.authorization.authenticated-only", SoloAutenticacionProperties.class).get();

        // Assert
        assertThat(propiedades.entries()).extracting(Entrada::clave).contains("GET /api/usuarios/mi/perfil");
    }
}
