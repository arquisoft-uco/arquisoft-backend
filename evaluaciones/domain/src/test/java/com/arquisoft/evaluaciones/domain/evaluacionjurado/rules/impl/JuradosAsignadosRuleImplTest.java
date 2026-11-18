package com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosNoAsignadosException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.JuradosAsignadosEvaluacion;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JuradosAsignadosRuleImplTest {

    private final JuradosAsignadosRuleImpl regla = new JuradosAsignadosRuleImpl();

    @Test
    void debePermitirFlujo_cuandoTodosLosJuradosEstanAsignados() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> regla.validar(new JuradosAsignadosEvaluacion(Set.of(jurado), Set.of(jurado, otro))))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarConDetalle_cuandoAlgunJuradoNoEstaAsignado() {
        // Arrange
        var asignado = UtilUUID.generarNuevoUUID();
        var ajeno = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(
                new JuradosAsignadosEvaluacion(Set.of(asignado, ajeno), Set.of(asignado))))
                .isInstanceOfSatisfying(JuradosNoAsignadosException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.EvaluacionJurado.JURADOS_NO_ASIGNADOS);
                    assertThat(exception.getMessage())
                            .contains(ajeno.toString())
                            .doesNotContain(asignado.toString());
                    assertThat(exception.getError().getMessage()).doesNotContain(ajeno.toString());
                });
    }
}
