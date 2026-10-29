package com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EstadoDestinoEntregableNoPermitidoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.TransicionEstadoEntregable;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoDestinoEntregablePermitidoRuleImplTest {

    private final EstadoDestinoEntregablePermitidoRuleImpl rule = new EstadoDestinoEntregablePermitidoRuleImpl();

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class, names = {"CANCELADO", "CERRADO"})
    void debePasar_cuandoElDestinoEsCanceladoOCerrado(EstadoEntregable destino) {
        // Arrange
        var transicion = new TransicionEstadoEntregable(
                UtilUUID.generarNuevoUUID(), EstadoEntregable.APROBADO, destino);

        // Act & Assert
        assertThatCode(() -> rule.validar(transicion)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoEntregable.class,
            names = {"DISPONIBLE_EVALUACION", "EN_EVALUACION", "APROBADO", "APROBADO_CON_OBSERVACIONES", "RECHAZADO"})
    void debeLanzar_cuandoElDestinoNoEsCanceladoNiCerrado(EstadoEntregable destino) {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var transicion = new TransicionEstadoEntregable(id, EstadoEntregable.APROBADO, destino);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(transicion))
                .isInstanceOfSatisfying(EstadoDestinoEntregableNoPermitidoException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(EntregablesCodes.EntregableProyectoGrado.ESTADO_DESTINO_NO_PERMITIDO);
                    assertThat(ex.getError().getMessage()).doesNotContain(id.toString());
                });
    }
}
