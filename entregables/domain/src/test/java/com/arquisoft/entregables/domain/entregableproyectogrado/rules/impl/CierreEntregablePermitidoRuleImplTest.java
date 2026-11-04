package com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.CierreEntregableNoPermitidoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.TransicionEstadoEntregable;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CierreEntregablePermitidoRuleImplTest {

    private final CierreEntregablePermitidoRuleImpl rule = new CierreEntregablePermitidoRuleImpl();

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class, names = {"APROBADO", "APROBADO_CON_OBSERVACIONES"})
    void debePasar_cuandoCierraDesdeUnEstadoAprobado(EstadoEntregable actual) {
        // Arrange
        var transicion = new TransicionEstadoEntregable(
                UtilUUID.generarNuevoUUID(), actual, EstadoEntregable.CERRADO);

        // Act & Assert
        assertThatCode(() -> rule.validar(transicion)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class,
            names = {"DISPONIBLE_EVALUACION", "EN_EVALUACION", "RECHAZADO", "CANCELADO", "CERRADO"})
    void debeLanzar_cuandoCierraDesdeOtroEstado(EstadoEntregable actual) {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var transicion = new TransicionEstadoEntregable(id, actual, EstadoEntregable.CERRADO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(transicion))
                .isInstanceOfSatisfying(CierreEntregableNoPermitidoException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(EntregablesCodes.EntregableProyectoGrado.CIERRE_NO_PERMITIDO);
                    assertThat(ex.getError().getMessage()).doesNotContain(id.toString());
                });
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class, names = {"CANCELADO", "EN_EVALUACION"})
    void debePasar_cuandoElDestinoNoEsCerrado(EstadoEntregable destino) {
        // Arrange
        var transicion = new TransicionEstadoEntregable(
                UtilUUID.generarNuevoUUID(), EstadoEntregable.RECHAZADO, destino);

        // Act & Assert
        assertThatCode(() -> rule.validar(transicion)).doesNotThrowAnyException();
    }
}
