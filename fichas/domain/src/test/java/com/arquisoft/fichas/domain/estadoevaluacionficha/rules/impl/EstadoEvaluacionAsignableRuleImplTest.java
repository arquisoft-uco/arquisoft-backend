package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoDescartadaNoAsignableException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionAprobadaConObservacionesException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionSinObservacionesException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.AsignacionEstadoEvaluacion;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoEvaluacionAsignableRuleImplTest {

    private final EstadoEvaluacionAsignableRuleImpl regla = new EstadoEvaluacionAsignableRuleImpl();

    @Test
    void debePasar_cuandoEsAprobadaSinObservaciones() {
        // Arrange
        var asignacion = new AsignacionEstadoEvaluacion(UtilUUID.generarNuevoUUID(), EstadoEvaluacion.APROBADA, 0);

        // Act & Assert
        assertThatCode(() -> regla.validar(asignacion)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 3})
    void debeLanzarObservacionesNoPermitidas_cuandoEsAprobadaConObservaciones(long observaciones) {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asignacion = new AsignacionEstadoEvaluacion(evaluacion, EstadoEvaluacion.APROBADA, observaciones);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(asignacion))
                .isInstanceOfSatisfying(EvaluacionAprobadaConObservacionesException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(FichasCodes.EstadoEvaluacionFicha.OBSERVACIONES_NO_PERMITIDAS);
                    assertThat(ex.getError().getMessage()).doesNotContain(evaluacion.toString());
                    assertThat(ex.getMessage()).contains(evaluacion.toString());
                });
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 4})
    void debePasar_cuandoEsAprobadaConObservacionesYTieneAlMenosUna(long observaciones) {
        // Arrange
        var asignacion = new AsignacionEstadoEvaluacion(
                UtilUUID.generarNuevoUUID(), EstadoEvaluacion.APROBADA_CON_OBSERVACIONES, observaciones);

        // Act & Assert
        assertThatCode(() -> regla.validar(asignacion)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarObservacionesRequeridas_cuandoEsAprobadaConObservacionesSinNinguna() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asignacion = new AsignacionEstadoEvaluacion(evaluacion, EstadoEvaluacion.APROBADA_CON_OBSERVACIONES, 0);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(asignacion))
                .isInstanceOfSatisfying(EvaluacionSinObservacionesException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(FichasCodes.EstadoEvaluacionFicha.OBSERVACIONES_REQUERIDAS);
                    assertThat(ex.getError().getMessage()).doesNotContain(evaluacion.toString());
                });
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 5})
    void debePasar_cuandoEsNoAprobadaConCualquierCantidadDeObservaciones(long observaciones) {
        // Arrange
        var asignacion = new AsignacionEstadoEvaluacion(
                UtilUUID.generarNuevoUUID(), EstadoEvaluacion.NO_APROBADA, observaciones);

        // Act & Assert
        assertThatCode(() -> regla.validar(asignacion)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 2})
    void debeLanzarDescartadaNoAsignable_cuandoSePideDescartada(long observaciones) {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asignacion = new AsignacionEstadoEvaluacion(evaluacion, EstadoEvaluacion.DESCARTADA, observaciones);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(asignacion))
                .isInstanceOf(DomainException.class)
                .isInstanceOfSatisfying(EstadoDescartadaNoAsignableException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(FichasCodes.EstadoEvaluacionFicha.DESCARTADA_NO_ASIGNABLE);
                    assertThat(ex.getError().getMessage()).doesNotContain(evaluacion.toString());
                });
    }
}
