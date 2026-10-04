package com.arquisoft.fichas.domain.observacionevaluacion.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.observacionevaluacion.exception.EvaluacionFichaCerradaException;
import com.arquisoft.fichas.domain.observacionevaluacion.model.EstadoEvaluacionObservada;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionFichaAbiertaRuleImplTest {

    private final EvaluacionFichaAbiertaRuleImpl regla = new EvaluacionFichaAbiertaRuleImpl();

    @ParameterizedTest
    @EnumSource(value = EstadoEvaluacion.class,
            names = {"APROBADA", "APROBADA_CON_OBSERVACIONES", "NO_APROBADA", "DESCARTADA"})
    void debeLanzarCerrada_cuandoElUltimoEstadoEsTerminal(EstadoEvaluacion estadoTerminal) {
        // Arrange
        var estado = new EstadoEvaluacionObservada(UtilUUID.generarNuevoUUID(), estadoTerminal);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(estado))
                .isInstanceOf(EvaluacionFichaCerradaException.class)
                .hasMessageContaining(estadoTerminal.getId())
                .extracting("codigoError")
                .isEqualTo(FichasCodes.ObservacionEvaluacion.EVALUACION_CERRADA);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEvaluacion.class, names = {"EN_EVALUACION", "VACIO"})
    void debePasar_cuandoElUltimoEstadoNoEsTerminal(EstadoEvaluacion estadoAbierto) {
        // Arrange — VACIO no lanza aquí: la inexistencia ya la rechazó EvaluacionFichaExisteRule
        var estado = new EstadoEvaluacionObservada(UtilUUID.generarNuevoUUID(), estadoAbierto);

        // Act & Assert
        assertThatCode(() -> regla.validar(estado)).doesNotThrowAnyException();
    }
}
