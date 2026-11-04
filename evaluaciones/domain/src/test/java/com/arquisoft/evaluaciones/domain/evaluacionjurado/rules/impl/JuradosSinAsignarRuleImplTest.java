package com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosYaAsignadosException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.DisponibilidadJuradosEvaluacion;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JuradosSinAsignarRuleImplTest {

    private final JuradosSinAsignarRuleImpl regla = new JuradosSinAsignarRuleImpl();

    @Test
    void debePermitirFlujo_cuandoNingunJuradoEstaAsignado() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var solicitado = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> regla.validar(new DisponibilidadJuradosEvaluacion(
                evaluacion, Set.of(solicitado), Set.of())))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcionNombrandoDuplicados_cuandoAlgunJuradoYaEstaAsignado() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asignado = UtilUUID.generarNuevoUUID();
        var libre = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new DisponibilidadJuradosEvaluacion(
                evaluacion, Set.of(asignado, libre), Set.of(asignado))))
                .isInstanceOfSatisfying(JuradosYaAsignadosException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionJurado.JURADOS_YA_ASIGNADOS);
                    assertThat(exception.getMessage())
                            .contains(asignado.toString())
                            .doesNotContain(libre.toString());
                });
    }
}
