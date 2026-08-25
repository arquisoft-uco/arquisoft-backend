package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.CupoEstudiantesExcedidoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.CupoEstudiantesFicha;
import com.arquisoft.shared.message.constant.FichasLimits;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteFichaPerfilCupoDisponibleRuleImplTest {

    private final EstudianteFichaPerfilCupoDisponibleRuleImpl regla =
            new EstudianteFichaPerfilCupoDisponibleRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoLosNuevosVinculosExcedenElCupo() {
        // Arrange
        var cupo = new CupoEstudiantesFicha(FichasLimits.FichaPerfil.ESTUDIANTES_MAX, 1);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(cupo))
                .isInstanceOf(CupoEstudiantesExcedidoException.class);
    }

    @Test
    void debePasar_cuandoLosNuevosVinculosLlenanElCupoSinExcederlo() {
        // Arrange
        var cupo = new CupoEstudiantesFicha(FichasLimits.FichaPerfil.ESTUDIANTES_MAX - 1L, 1);

        // Act & Assert
        assertThatCode(() -> regla.validar(cupo)).doesNotThrowAnyException();
    }
}
