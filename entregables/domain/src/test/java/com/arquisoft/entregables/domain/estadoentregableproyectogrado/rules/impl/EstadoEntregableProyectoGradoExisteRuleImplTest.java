package com.arquisoft.entregables.domain.estadoentregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.estadoentregableproyectogrado.exception.EstadoEntregableProyectoGradoNoEncontradoException;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.model.ExistenciaEstadoEntregableProyectoGrado;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoEntregableProyectoGradoExisteRuleImplTest {

    private final EstadoEntregableProyectoGradoExisteRuleImpl rule = new EstadoEntregableProyectoGradoExisteRuleImpl();

    @Test
    void debeLanzarSinIdEnElMensajeDeCliente_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new ExistenciaEstadoEntregableProyectoGrado(id, false)))
                .isInstanceOfSatisfying(EstadoEntregableProyectoGradoNoEncontradoException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(EntregablesCodes.EstadoEntregableProyectoGrado.NO_ENCONTRADO);
                    assertThat(ex.getError().getMessage()).doesNotContain(id.toString());
                    assertThat(ex.getMessage()).contains(id.toString());
                });
    }

    @Test
    void debePasar_cuandoExiste() {
        // Act & Assert
        assertThatCode(() -> rule.validar(
                new ExistenciaEstadoEntregableProyectoGrado(UtilUUID.generarNuevoUUID(), true)))
                .doesNotThrowAnyException();
    }
}
