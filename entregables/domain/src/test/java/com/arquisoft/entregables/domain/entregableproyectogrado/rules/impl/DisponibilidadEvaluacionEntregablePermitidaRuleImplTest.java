package com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.DisponibilidadEvaluacionEntregableNoPermitidaException;
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

class DisponibilidadEvaluacionEntregablePermitidaRuleImplTest {

    private final DisponibilidadEvaluacionEntregablePermitidaRuleImpl rule =
            new DisponibilidadEvaluacionEntregablePermitidaRuleImpl();

    @Test
    void debePasar_cuandoActualEsNuevo() {
        // Arrange
        var transicion = new TransicionEstadoEntregable(
                UtilUUID.generarNuevoUUID(), EstadoEntregable.NUEVO, EstadoEntregable.DISPONIBLE_EVALUACION);

        // Act & Assert
        assertThatCode(() -> rule.validar(transicion)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class, names = {"NUEVO"}, mode = EnumSource.Mode.EXCLUDE)
    void debeLanzar_cuandoActualNoEsNuevo(EstadoEntregable actual) {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var transicion = new TransicionEstadoEntregable(id, actual, EstadoEntregable.DISPONIBLE_EVALUACION);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(transicion))
                .isInstanceOfSatisfying(DisponibilidadEvaluacionEntregableNoPermitidaException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(EntregablesCodes.EntregableProyectoGrado.DISPONIBILIDAD_EVALUACION_NO_PERMITIDA);
                    assertThat(ex.getError().getMessage()).doesNotContain(id.toString());
                    assertThat(ex.getMessage()).contains(id.toString());
                });
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class, names = {"CANCELADO", "CERRADO"})
    void debePasar_cuandoElDestinoNoEsDisponibleEvaluacion(EstadoEntregable destino) {
        // Arrange
        var transicion = new TransicionEstadoEntregable(
                UtilUUID.generarNuevoUUID(), EstadoEntregable.APROBADO, destino);

        // Act & Assert
        assertThatCode(() -> rule.validar(transicion)).doesNotThrowAnyException();
    }
}
