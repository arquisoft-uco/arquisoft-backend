package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionVersionAnteriorNoResueltaException;
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

class RevisionVersionAnteriorResueltaRuleImplTest {

    private static RevisionAsesorDomain revision(UUID id, EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(
                id, UtilUUID.generarNuevoUUID(), estado, UtilFecha.generarInstanteActual());
    }

    @Test
    void noDebeLanzar_cuandoEsLaPrimeraVersion() {
        // Arrange
        var regla = new RevisionVersionAnteriorResueltaRuleImpl();
        var entrada = new RevisionVersionAnterior(0, RevisionAsesorDomain.VACIO);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoLaRevisionAnteriorEstaResuelta() {
        // Arrange
        var regla = new RevisionVersionAnteriorResueltaRuleImpl();
        var entrada = new RevisionVersionAnterior(2,
                revision(UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.RESUELTA));

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoRevisionAsesor.class, names = {"PENDIENTE", "EN_PROGRESO", "CERRADA"})
    void debeLanzar_cuandoLaRevisionAnteriorNoEstaResuelta(EstadoRevisionAsesor estado) {
        // Arrange
        var regla = new RevisionVersionAnteriorResueltaRuleImpl();
        var id = UtilUUID.generarNuevoUUID();
        var entrada = new RevisionVersionAnterior(2, revision(id, estado));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionVersionAnteriorNoResueltaException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(ArtefactosCodes.RevisionAsesor.VERSION_ANTERIOR_NO_RESUELTA);
                    assertThat(ex.getError().getMessage()).isNotBlank()
                            .doesNotContain(id.toString(), "??").contains(estado.getNombre());
                    assertThat(ex.getMessage()).contains(id.toString(), estado.getId());
                });
    }
}
