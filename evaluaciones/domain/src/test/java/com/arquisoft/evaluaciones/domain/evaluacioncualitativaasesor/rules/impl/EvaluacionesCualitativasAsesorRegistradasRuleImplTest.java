package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionesCualitativasAsesorNoRegistradasException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionesCualitativasAsesorRegistradas;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionesCualitativasAsesorRegistradasRuleImplTest {

    private final EvaluacionesCualitativasAsesorRegistradasRuleImpl regla =
            new EvaluacionesCualitativasAsesorRegistradasRuleImpl();

    @Test
    void noDebeLanzar_cuandoTodosRegistrados() {
        // Arrange
        var item1 = UtilUUID.generarNuevoUUID();
        var item2 = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaEvaluacionesCualitativasAsesorRegistradas(
                UtilUUID.generarNuevoUUID(), Set.of(item1), Set.of(item1, item2));

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarConFaltantesSoloEnDetalle_cuandoAlgunItemNoRegistrado() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var registrado = UtilUUID.generarNuevoUUID();
        var faltante = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaEvaluacionesCualitativasAsesorRegistradas(
                evaluacionAsesor, Set.of(registrado, faltante), Set.of(registrado));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(EvaluacionesCualitativasAsesorNoRegistradasException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACIONES_NO_REGISTRADAS);
                    assertThat(exception.getError().getDetalle())
                            .contains(faltante.toString())
                            .doesNotContain(registrado.toString());
                    assertThat(exception.getError().getMessage())
                            .doesNotContain(faltante.toString())
                            .doesNotContain(evaluacionAsesor.toString());
                });
    }
}
