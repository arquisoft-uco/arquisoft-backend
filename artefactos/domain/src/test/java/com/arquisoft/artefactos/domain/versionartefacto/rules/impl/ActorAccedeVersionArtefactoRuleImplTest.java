package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.ActorSinAccesoVersionArtefactoException;
import com.arquisoft.artefactos.domain.versionartefacto.model.AccesoVersionArtefacto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActorAccedeVersionArtefactoRuleImplTest {

    private final ActorAccedeVersionArtefactoRuleImpl regla = new ActorAccedeVersionArtefactoRuleImpl();
    private final UUID version = UtilUUID.generarNuevoUUID();
    private final UUID actor = UtilUUID.generarNuevoUUID();
    private final UUID otro = UtilUUID.generarNuevoUUID();

    @Test
    void debePermitir_cuandoEsAsesorAsignado() {
        // Arrange
        var entrada = new AccesoVersionArtefacto(version, actor, true, false, actor, false);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debePermitir_cuandoEsEstudianteQueParticipa() {
        // Arrange
        var entrada = new AccesoVersionArtefacto(version, actor, false, true, otro, true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoAsesorDistinto() {
        // Arrange
        var entrada = new AccesoVersionArtefacto(version, actor, true, false, otro, true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(ActorSinAccesoVersionArtefactoException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.VersionArtefacto.ACCESO_DENEGADO);
    }

    @Test
    void debeLanzar_cuandoEstudianteNoParticipaAunqueSeaElAsesorRegistrado() {
        // Arrange
        var entrada = new AccesoVersionArtefacto(version, actor, false, true, actor, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(ActorSinAccesoVersionArtefactoException.class);
    }

    @Test
    void debeLanzar_cuandoNoTieneNingunRol() {
        // Arrange
        var entrada = new AccesoVersionArtefacto(version, actor, false, false, actor, true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(ActorSinAccesoVersionArtefactoException.class);
    }

    @Test
    void debeNoExponerUuidEnMensaje_yLlevarloEnElDetalle_cuandoLanza() {
        // Arrange
        var entrada = new AccesoVersionArtefacto(version, actor, true, true, otro, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(ActorSinAccesoVersionArtefactoException.class, ex -> {
                    assertThat(ex.getError().getMessage()).doesNotContain(actor.toString(), version.toString());
                    assertThat(ex.getMessage()).contains(actor.toString(), version.toString());
                });
    }
}
