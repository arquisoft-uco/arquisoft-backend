package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.RevisionAnteriorNoPermiteEliminarVersionException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionAnteriorPermiteEliminarVersionRuleImplTest {

    private static RevisionAsesorDomain revision(UUID id, EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(
                id, UtilUUID.generarNuevoUUID(), estado, UtilFecha.generarInstanteActual());
    }

    @Test
    void debeNoLanzar_cuandoNoHayVersionAnterior() {
        // Arrange
        var regla = new RevisionAnteriorPermiteEliminarVersionRuleImpl();
        var entrada = new RevisionVersionAnterior(0, RevisionAsesorDomain.VACIO);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeNoLanzar_cuandoLaRevisionAnteriorEstaEnProgreso() {
        // Arrange
        var regla = new RevisionAnteriorPermiteEliminarVersionRuleImpl();
        var entrada = new RevisionVersionAnterior(1,
                revision(UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.EN_PROGRESO));

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoRevisionAsesor.class, names = {"PENDIENTE", "RESUELTA", "CERRADA"})
    void debeLanzar_cuandoLaRevisionAnteriorNoEstaEnProgreso(EstadoRevisionAsesor estado) {
        // Arrange
        var regla = new RevisionAnteriorPermiteEliminarVersionRuleImpl();
        var id = UtilUUID.generarNuevoUUID();
        var entrada = new RevisionVersionAnterior(1, revision(id, estado));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionAnteriorNoPermiteEliminarVersionException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(ArtefactosCodes.VersionArtefacto.REVISION_ANTERIOR_NO_PERMITE_ELIMINAR);
                    assertThat(ex.getError().getMessage()).isNotBlank()
                            .doesNotContain(id.toString(), "??").contains(estado.getNombre());
                    assertThat(ex.getMessage()).contains(id.toString(), estado.getId());
                });
    }
}
