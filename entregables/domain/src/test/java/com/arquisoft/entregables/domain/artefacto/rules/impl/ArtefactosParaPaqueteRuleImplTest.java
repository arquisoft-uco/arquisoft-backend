package com.arquisoft.entregables.domain.artefacto.rules.impl;

import com.arquisoft.entregables.domain.artefacto.exception.ProyectoSinArtefactosParaPaqueteException;
import com.arquisoft.entregables.domain.artefacto.model.ArtefactosDelProyecto;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArtefactosParaPaqueteRuleImplTest {

    private final ArtefactosParaPaqueteRuleImpl rule = new ArtefactosParaPaqueteRuleImpl();

    @Test
    void debePermitir_cuandoHayArtefactosVigentes() {
        // Arrange
        var entrada = new ArtefactosDelProyecto(UtilUUID.generarNuevoUUID(), "Proyecto", 2);

        // Act & Assert
        assertThatCode(() -> rule.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSinIdsEnElMensaje_cuandoNoHayArtefactosVigentes() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var entrada = new ArtefactosDelProyecto(proyecto, "Proyecto Uno", 0);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(entrada))
                .isInstanceOfSatisfying(ProyectoSinArtefactosParaPaqueteException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(EntregablesCodes.Artefacto.SIN_ARTEFACTOS_PARA_PAQUETE);
                    assertThat(ex.getError().getMessage()).doesNotContain(proyecto.toString());
                });
    }
}
