package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilSinEvaluacionFinalizadaException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EvaluacionesFinalizadasFicha;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ConteoEvaluacionesPorEstado;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.ResumenEvaluacionesFicha;
import com.arquisoft.shared.message.constant.FichasCodes;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FichaPerfilConEvaluacionFinalizadaRuleImplTest {

    private static final String TITULO = "Sistema de gestión de tesis";

    private final FichaPerfilConEvaluacionFinalizadaRuleImpl regla = new FichaPerfilConEvaluacionFinalizadaRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoSoloHayEvaluacionesEnEvaluacionODescartadas() {
        // Arrange
        var ficha = UUID.randomUUID();
        var resumen = new ResumenEvaluacionesFicha(ficha, List.of(
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.EN_EVALUACION, 2, 0),
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.DESCARTADA, 1, 1)));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new EvaluacionesFinalizadasFicha(TITULO, resumen)))
                .isInstanceOfSatisfying(FichaPerfilSinEvaluacionFinalizadaException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(FichasCodes.EstadoFichaPerfil.SIN_EVALUACION_FINALIZADA);
                    assertThat(ex.getError().getMessage()).contains(TITULO).doesNotContain(ficha.toString());
                });
    }

    @Test
    void debePasar_cuandoHayUnaEvaluacionNoAprobada() {
        // Arrange
        var resumen = new ResumenEvaluacionesFicha(UUID.randomUUID(), List.of(
                new ConteoEvaluacionesPorEstado(EstadoEvaluacion.NO_APROBADA, 1, 0)));

        // Act & Assert
        assertThatCode(() -> regla.validar(new EvaluacionesFinalizadasFicha(TITULO, resumen))).doesNotThrowAnyException();
    }
}
