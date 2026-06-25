package com.arquisoft.fichas.domain.estadoficha.aggregate;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoFichaAggregateTest {

    @Test
    void debeReconstruir_cuandoDatosValidos() {
        // Arrange
        UUID id = UUID.randomUUID();
        String nombre = "En Construccion";
        String descripcion = "Estado inicial";

        // Act
        EstadoFichaAggregate aggregate = EstadoFichaAggregate.reconstruir(id, nombre, descripcion);

        // Assert
        assertThat(aggregate).isNotNull();
        assertThat(aggregate.getId()).isEqualTo(id);
        assertThat(aggregate.getNombre()).isEqualTo(nombre);
        assertThat(aggregate.getDescripcion()).isEqualTo(descripcion);
    }
}
