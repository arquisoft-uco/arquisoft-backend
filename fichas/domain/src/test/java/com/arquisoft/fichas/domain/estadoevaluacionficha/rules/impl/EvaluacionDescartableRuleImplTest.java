package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionNoDescartableException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.UltimoEstadoEvaluacion;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionDescartableRuleImplTest {

    private final EvaluacionDescartableRuleImpl regla = new EvaluacionDescartableRuleImpl();

    @Test
    void debePasar_cuandoEstadoEsEnEvaluacion() {
        // Arrange
        var ultimo = new UltimoEstadoEvaluacion(UtilUUID.generarNuevoUUID(), EstadoEvaluacion.EN_EVALUACION);

        // Act & Assert
        assertThatCode(() -> regla.validar(ultimo)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEvaluacion.class, names = {
            "APROBADA", "APROBADA_CON_OBSERVACIONES", "NO_APROBADA", "DESCARTADA", "VACIO"})
    void debeLanzarNoDescartable_cuandoEstadoNoEsEnEvaluacion(EstadoEvaluacion estado) {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var ultimo = new UltimoEstadoEvaluacion(evaluacion, estado);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(ultimo))
                .isInstanceOfSatisfying(EvaluacionNoDescartableException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(FichasCodes.EstadoEvaluacionFicha.EVALUACION_NO_DESCARTABLE);
                    assertThat(ex.getError().getMessage()).doesNotContain(evaluacion.toString());
                });
    }
}
