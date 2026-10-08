package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilSinEvaluacionFinalizadaException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ConteoEvaluacionesPorEstado;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ResumenEvaluacionesFicha;
import com.arquisoft.shared.message.constant.FichasCodes;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FichaPerfilConEvaluacionFinalizadaRuleImplTest {

    private final FichaPerfilConEvaluacionFinalizadaRuleImpl regla = new FichaPerfilConEvaluacionFinalizadaRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoSoloHayEvaluacionesEnEvaluacionODescartadas() {
        // Arrange
        var resumen = new ResumenEvaluacionesFicha(UUID.randomUUID(), List.of(
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.EN_EVALUACION, 2, 0),
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.DESCARTADA, 1, 1)));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(resumen))
                .isInstanceOf(FichaPerfilSinEvaluacionFinalizadaException.class)
                .extracting("codigoError")
                .isEqualTo(FichasCodes.EstadoFichaPerfil.SIN_EVALUACION_FINALIZADA);
    }

    @Test
    void debePasar_cuandoHayUnaEvaluacionNoAprobada() {
        // Arrange
        var resumen = new ResumenEvaluacionesFicha(UUID.randomUUID(), List.of(
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.NO_APROBADA, 1, 0)));

        // Act & Assert
        assertThatCode(() -> regla.validar(resumen)).doesNotThrowAnyException();
    }
}
