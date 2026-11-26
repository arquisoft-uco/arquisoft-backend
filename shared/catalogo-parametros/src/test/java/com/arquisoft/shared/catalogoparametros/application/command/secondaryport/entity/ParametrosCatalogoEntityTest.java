package com.arquisoft.shared.catalogoparametros.application.command.secondaryport.entity;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParametrosCatalogoEntityTest {

    @Test
    void debeCopiarElMapa_cuandoSeConstruye() {
        // Arrange
        var origen = new HashMap<String, String>();
        origen.put("A", "1");

        // Act
        var entity = new ParametrosCatalogoEntity(origen);
        origen.put("B", "2");

        // Assert
        assertThat(entity.valores()).containsExactly(Map.entry("A", "1"));
        assertThatThrownBy(() -> entity.valores().put("C", "3")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void debeQuedarVacio_cuandoElMapaEsNulo() {
        // Arrange / Act
        var entity = new ParametrosCatalogoEntity(null);

        // Assert
        assertThat(entity.valores()).isEmpty();
    }
}
