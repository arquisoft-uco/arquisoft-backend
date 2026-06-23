package com.arquisoft.fichas.domain.estudiante.aggregate;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteAggregateTest {

    @Test
    void debeReconstruir_cuandoDatosValidos() {
        // Arrange
        UUID id = UUID.randomUUID();
        String identificador = "1234567890";
        String nombre = "Juan Pérez";
        String email = "juan.perez@example.com";

        // Act
        EstudianteAggregate estudiante = EstudianteAggregate.reconstruir(id, identificador, nombre, email);

        // Assert
        assertThat(estudiante).isNotNull();
        assertThat(estudiante.getId()).isEqualTo(id);
        assertThat(estudiante.getIdentificador()).isEqualTo(identificador);
        assertThat(estudiante.getNombre()).isEqualTo(nombre);
        assertThat(estudiante.getEmail()).isEqualTo(email);
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        UUID id = UUID.randomUUID();
        String identificador = null;
        String nombre = null;
        String email = null;

        // Act
        EstudianteAggregate estudiante = EstudianteAggregate.reconstruir(id, identificador, nombre, email);

        // Assert
        assertThat(estudiante).isNotNull();
        assertThat(estudiante.getId()).isEqualTo(id);
        assertThat(estudiante.getIdentificador()).isNull();
        assertThat(estudiante.getNombre()).isNull();
        assertThat(estudiante.getEmail()).isNull();
    }
}
