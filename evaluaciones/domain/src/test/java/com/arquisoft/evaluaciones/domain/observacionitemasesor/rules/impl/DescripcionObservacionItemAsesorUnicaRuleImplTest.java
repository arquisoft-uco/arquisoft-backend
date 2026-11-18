package com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.DescripcionObservacionItemAsesorDuplicadaException;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.DisponibilidadDescripcionObservacionItemAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DescripcionObservacionItemAsesorUnicaRuleImplTest {

    private final DescripcionObservacionItemAsesorUnicaRuleImpl rule =
            new DescripcionObservacionItemAsesorUnicaRuleImpl();

    @Test
    void noDebeLanzar_cuandoDisponible() {
        // Arrange
        var disponibilidad = new DisponibilidadDescripcionObservacionItemAsesor(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoYaExiste() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var disponibilidad = new DisponibilidadDescripcionObservacionItemAsesor(evaluacion, true);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOfSatisfying(DescripcionObservacionItemAsesorDuplicadaException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ObservacionItemAsesor.DESCRIPCION_DUPLICADA);
                    assertThat(exception.getMessage()).contains(evaluacion.toString());
                });
    }
}
