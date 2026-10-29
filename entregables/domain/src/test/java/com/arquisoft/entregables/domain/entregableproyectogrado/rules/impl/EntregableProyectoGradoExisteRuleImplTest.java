package com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EntregableProyectoGradoNoEncontradoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.ExistenciaEntregableProyectoGrado;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntregableProyectoGradoExisteRuleImplTest {

    private final EntregableProyectoGradoExisteRuleImpl rule = new EntregableProyectoGradoExisteRuleImpl();

    @Test
    void debeLanzarSinIdEnElMensajeDeCliente_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new ExistenciaEntregableProyectoGrado(id, false)))
                .isInstanceOfSatisfying(EntregableProyectoGradoNoEncontradoException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(EntregablesCodes.EntregableProyectoGrado.NO_ENCONTRADO);
                    assertThat(ex.getError().getMessage()).doesNotContain(id.toString());
                    assertThat(ex.getMessage()).contains(id.toString());
                });
    }

    @Test
    void debePasar_cuandoExiste() {
        // Act & Assert
        assertThatCode(() -> rule.validar(new ExistenciaEntregableProyectoGrado(UtilUUID.generarNuevoUUID(), true)))
                .doesNotThrowAnyException();
    }
}
