package com.arquisoft.fichas.domain.estudiante.rules.impl;

import com.arquisoft.fichas.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.domain.estudiante.model.ExistenciaEstudiantes;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantesExistenRuleImplTest {

    private final EstudiantesExistenRuleImpl regla = new EstudiantesExistenRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoAlgunEstudianteNoExiste() {
        // Arrange
        UUID existente = UUID.randomUUID();
        UUID inexistente = UUID.randomUUID();
        var existencia = new ExistenciaEstudiantes(List.of(existente, inexistente), List.of(existente));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(EstudianteNoEncontradoException.class)
                .hasMessageContaining(inexistente.toString());
    }

    @Test
    void debePasar_cuandoTodosLosEstudiantesExisten() {
        // Arrange
        List<UUID> estudiantes = List.of(UUID.randomUUID(), UUID.randomUUID());
        var existencia = new ExistenciaEstudiantes(estudiantes, estudiantes);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoNoSeSolicitaNingunEstudiante() {
        // Arrange
        var existencia = new ExistenciaEstudiantes(List.of(), List.of());

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }
}
