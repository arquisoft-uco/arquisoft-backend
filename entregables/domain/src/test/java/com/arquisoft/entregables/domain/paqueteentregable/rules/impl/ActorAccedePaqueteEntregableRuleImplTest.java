package com.arquisoft.entregables.domain.paqueteentregable.rules.impl;

import com.arquisoft.entregables.domain.paqueteentregable.exception.ActorSinAccesoPaqueteEntregableException;
import com.arquisoft.entregables.domain.paqueteentregable.model.AccesoPaqueteEntregable;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActorAccedePaqueteEntregableRuleImplTest {

    private final ActorAccedePaqueteEntregableRuleImpl rule = new ActorAccedePaqueteEntregableRuleImpl();

    @Test
    void debePermitir_cuandoAsesorPropietario() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var entrada = new AccesoPaqueteEntregable(UtilUUID.generarNuevoUUID(), actor, true, false, actor, false);

        // Act & Assert
        assertThatCode(() -> rule.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debePermitir_cuandoEstudianteParticipa() {
        // Arrange
        var entrada = new AccesoPaqueteEntregable(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                false, true, UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoAsesorNoEsPropietario() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var actor = UtilUUID.generarNuevoUUID();
        var entrada = new AccesoPaqueteEntregable(entregable, actor, true, false, UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(entrada))
                .isInstanceOfSatisfying(ActorSinAccesoPaqueteEntregableException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(EntregablesCodes.PaqueteEntregable.ACCESO_DENEGADO);
                    assertThat(ex.getError().getMessage()).doesNotContain(entregable.toString(), actor.toString());
                    assertThat(ex.getMessage()).contains(entregable.toString(), actor.toString());
                });
    }

    @Test
    void debeLanzar_cuandoEstudianteNoParticipa() {
        // Arrange
        var entrada = new AccesoPaqueteEntregable(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                false, true, UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(entrada)).isInstanceOf(ActorSinAccesoPaqueteEntregableException.class);
    }

    @Test
    void debeLanzar_cuandoRolNoCoincideConLaCondicion() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var entrada = new AccesoPaqueteEntregable(UtilUUID.generarNuevoUUID(), actor, false, false, actor, true);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(entrada)).isInstanceOf(ActorSinAccesoPaqueteEntregableException.class);
    }
}
