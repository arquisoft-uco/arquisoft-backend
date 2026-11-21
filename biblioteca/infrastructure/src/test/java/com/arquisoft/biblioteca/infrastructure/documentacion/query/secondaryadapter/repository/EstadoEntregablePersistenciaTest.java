package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import com.arquisoft.biblioteca.domain.estadoentregable.EstadoEntregable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoEntregablePersistenciaTest {

    @Test
    void debeCoincidirConElEnumDeDominio_enIdYNombre() {
        // Arrange
        var persistencia = Arrays.stream(EstadoEntregablePersistencia.values())
                .map(estado -> estado.getId() + "|" + estado.getNombre()).toList();
        var dominio = Arrays.stream(EstadoEntregable.values())
                .filter(estado -> estado != EstadoEntregable.VACIO)
                .map(estado -> estado.getId() + "|" + estado.getNombre()).toList();

        // Act & Assert
        assertThat(persistencia).containsExactlyInAnyOrderElementsOf(dominio);
        assertThat(dominio).containsExactlyInAnyOrderElementsOf(persistencia);
    }

    @ParameterizedTest
    @EnumSource(EstadoEntregablePersistencia.class)
    void debeDevolverNombre_cuandoIdValido(EstadoEntregablePersistencia estado) {
        // Act
        var nombre = EstadoEntregablePersistencia.nombreDe(estado.getId());

        // Assert
        assertThat(nombre).isEqualTo(estado.getNombre());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"DESCONOCIDO", "Aprobado"})
    void debeLanzarIntegridad_cuandoIdDesconocido(String id) {
        // Act & Assert
        assertThatThrownBy(() -> EstadoEntregablePersistencia.nombreDe(id))
                .isInstanceOf(IllegalStateException.class);
    }
}
