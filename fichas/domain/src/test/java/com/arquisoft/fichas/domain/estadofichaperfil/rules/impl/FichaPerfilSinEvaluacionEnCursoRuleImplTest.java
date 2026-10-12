package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilConEvaluacionEnCursoException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EvaluacionesEnCursoFicha;
import com.arquisoft.shared.message.constant.FichasCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FichaPerfilSinEvaluacionEnCursoRuleImplTest {

    private final FichaPerfilSinEvaluacionEnCursoRuleImpl regla = new FichaPerfilSinEvaluacionEnCursoRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoSaleDeDisponibleConEvaluacionesEnCurso() {
        // Arrange
        var evaluaciones = new EvaluacionesEnCursoFicha(UUID.randomUUID(),
                EstadoFicha.DISPONIBLE_PARA_EVALUACION, 2);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(evaluaciones))
                .isInstanceOf(FichaPerfilConEvaluacionEnCursoException.class)
                .extracting("codigoError")
                .isEqualTo(FichasCodes.EstadoFichaPerfil.EVALUACION_EN_CURSO);
    }

    @Test
    void debePasar_cuandoElEstadoActualNoEsDisponibleAunqueHayaEvaluacionesEnCurso() {
        // Arrange
        var evaluaciones = new EvaluacionesEnCursoFicha(UUID.randomUUID(), EstadoFicha.EN_CONSTRUCCION, 3);

        // Act & Assert
        assertThatCode(() -> regla.validar(evaluaciones)).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoEstaDisponibleSinEvaluacionesEnCurso() {
        // Arrange
        var evaluaciones = new EvaluacionesEnCursoFicha(UUID.randomUUID(),
                EstadoFicha.DISPONIBLE_PARA_EVALUACION, 0);

        // Act & Assert
        assertThatCode(() -> regla.validar(evaluaciones)).doesNotThrowAnyException();
    }
}
