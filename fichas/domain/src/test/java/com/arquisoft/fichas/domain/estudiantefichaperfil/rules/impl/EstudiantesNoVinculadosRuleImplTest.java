package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculosEstudiantesFicha;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudiantesNoVinculadosRuleImplTest {

    private final EstudiantesNoVinculadosRuleImpl regla = new EstudiantesNoVinculadosRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoAlgunEstudianteYaEstaVinculado() {
        // Arrange
        UUID yaVinculado = UUID.randomUUID();
        var vinculos = new VinculosEstudiantesFicha(List.of(yaVinculado));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vinculos))
                .isInstanceOf(EstudianteDuplicadoException.class)
                .hasMessageContaining(yaVinculado.toString());
    }

    @Test
    void debePasar_cuandoNingunEstudianteEstaVinculado() {
        // Arrange
        var vinculos = new VinculosEstudiantesFicha(List.of());

        // Act & Assert
        assertThatCode(() -> regla.validar(vinculos)).doesNotThrowAnyException();
    }
}
