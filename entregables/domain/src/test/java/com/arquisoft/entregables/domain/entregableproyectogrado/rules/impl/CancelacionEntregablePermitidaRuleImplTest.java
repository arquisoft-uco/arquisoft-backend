package com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.CancelacionEntregableNoPermitidaException;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.TransicionEstadoEntregable;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CancelacionEntregablePermitidaRuleImplTest {

    private final CancelacionEntregablePermitidaRuleImpl rule = new CancelacionEntregablePermitidaRuleImpl();

    @Test
    void debePermitir_cuandoCancelaDesdeDisponibleEvaluacion() {
        // Arrange
        var transicion = new TransicionEstadoEntregable(
                UtilUUID.generarNuevoUUID(), EstadoEntregable.DISPONIBLE_EVALUACION, EstadoEntregable.CANCELADO);

        // Act & Assert
        assertThatCode(() -> rule.validar(transicion)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class,
            names = {"EN_EVALUACION", "APROBADO", "APROBADO_CON_OBSERVACIONES"})
    void debeLanzar_cuandoCancelaDesdeUnEstadoDistintoDeDisponibleEvaluacion(EstadoEntregable actual) {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var transicion = new TransicionEstadoEntregable(id, actual, EstadoEntregable.CANCELADO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(transicion))
                .isInstanceOfSatisfying(CancelacionEntregableNoPermitidaException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(EntregablesCodes.EntregableProyectoGrado.CANCELACION_NO_PERMITIDA);
                    assertThat(ex.getError().getMessage()).doesNotContain(id.toString());
                    assertThat(ex.getMessage()).contains(id.toString());
                });
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class, names = {"CERRADO", "EN_EVALUACION"})
    void debeIgnorar_cuandoElDestinoNoEsCancelado(EstadoEntregable destino) {
        // Arrange
        var transicion = new TransicionEstadoEntregable(
                UtilUUID.generarNuevoUUID(), EstadoEntregable.APROBADO, destino);

        // Act & Assert
        assertThatCode(() -> rule.validar(transicion)).doesNotThrowAnyException();
    }
}
