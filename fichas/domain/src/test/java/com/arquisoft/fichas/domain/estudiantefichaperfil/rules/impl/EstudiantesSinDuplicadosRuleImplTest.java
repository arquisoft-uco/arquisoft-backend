package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantesSinDuplicadosRuleImplTest {

    private final EstudiantesSinDuplicadosRuleImpl regla = new EstudiantesSinDuplicadosRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoHayEstudianteRepetido() {
        // Arrange
        UUID repetido = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(List.of(repetido, repetido)))
                .isInstanceOf(EstudianteDuplicadoException.class)
                .hasMessageContaining(repetido.toString());
    }

    @Test
    void debePasar_cuandoNoHayRepetidos() {
        // Act & Assert
        assertThatCode(() -> regla.validar(List.of(UUID.randomUUID(), UUID.randomUUID())))
                .doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoListaEsNula() {
        // Act & Assert
        assertThatCode(() -> regla.validar(null)).doesNotThrowAnyException();
    }
}
