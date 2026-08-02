package com.arquisoft.fichas.domain.estudiante.rules.impl;

import com.arquisoft.fichas.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.domain.estudiante.port.out.EstudianteOutputPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantesExistenRuleImplTest {

    @Test
    void debeLanzarExcepcion_cuandoEstudianteNoExiste() {
        // Arrange
        UUID existente = UUID.randomUUID();
        UUID inexistente = UUID.randomUUID();
        var regla = new EstudiantesExistenRuleImpl(portConExistentes(Set.of(existente)));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(List.of(existente, inexistente)))
                .isInstanceOf(EstudianteNoEncontradoException.class)
                .hasMessageContaining(inexistente.toString());
    }

    @Test
    void debePasar_cuandoTodosLosEstudiantesExisten() {
        // Arrange
        UUID uno = UUID.randomUUID();
        UUID dos = UUID.randomUUID();
        var regla = new EstudiantesExistenRuleImpl(portConExistentes(Set.of(uno, dos)));

        // Act & Assert
        assertThatCode(() -> regla.validar(List.of(uno, dos))).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoListaEsNula() {
        // Arrange
        var regla = new EstudiantesExistenRuleImpl(portConExistentes(Set.of()));

        // Act & Assert
        assertThatCode(() -> regla.validar(null)).doesNotThrowAnyException();
    }

    private static EstudianteOutputPort portConExistentes(Set<UUID> existentes) {
        return existentes::contains;
    }
}
