package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.EvaluacionesCuantitativasAsesorNoRegistradasException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.ExistenciaEvaluacionesCuantitativasAsesorRegistradas;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionesCuantitativasAsesorRegistradasRuleImplTest {

    private final EvaluacionesCuantitativasAsesorRegistradasRuleImpl regla =
            new EvaluacionesCuantitativasAsesorRegistradasRuleImpl();

    @Test
    void noDebeLanzar_cuandoTodosRegistrados() {
        // Arrange
        var item1 = UtilUUID.generarNuevoUUID();
        var item2 = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaEvaluacionesCuantitativasAsesorRegistradas(
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
        var entrada = new ExistenciaEvaluacionesCuantitativasAsesorRegistradas(
                evaluacionAsesor, Set.of(registrado, faltante), Set.of(registrado));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(EvaluacionesCuantitativasAsesorNoRegistradasException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionCuantitativaAsesor.EVALUACIONES_NO_REGISTRADAS);
                    assertThat(exception.getError().getDetalle())
                            .contains(faltante.toString())
                            .doesNotContain(registrado.toString());
                    assertThat(exception.getError().getMessage())
                            .doesNotContain(faltante.toString())
                            .doesNotContain(evaluacionAsesor.toString());
                });
    }
}
