package com.arquisoft.artefactos.domain.revisionasesor.rules;

import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.ArtefactoCerradoException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionVersionAnteriorNoEnProgresoException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.VersionAnteriorSinRevisionException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.ArtefactoNoCerradoRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionVersionAnteriorEnProgresoRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.VersionAnteriorRevisadaRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PoliticaSubidaVersionRulesTest {

    private static RevisionAsesorDomain revisionEn(EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), estado, UtilFecha.generarInstanteActual());
    }

    @Test
    void noDebeLanzar_cuandoNoHayVersionesEnVersionAnteriorRevisada() {
        // Arrange
        var regla = new VersionAnteriorRevisadaRuleImpl();
        var entrada = new RevisionVersionAnterior(0, RevisionAsesorDomain.VACIO);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaUltimaVersionNoTieneRevision() {
        // Arrange
        var regla = new VersionAnteriorRevisadaRuleImpl();
        var entrada = new RevisionVersionAnterior(2, RevisionAsesorDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(VersionAnteriorSinRevisionException.class,
                        ex -> assertThat(ex.getCodigoError())
                                .isEqualTo(ArtefactosCodes.RevisionAsesor.VERSION_ANTERIOR_SIN_REVISION));
    }

    @Test
    void noDebeLanzar_cuandoLaUltimaVersionTieneRevision() {
        // Arrange
        var regla = new VersionAnteriorRevisadaRuleImpl();
        var entrada = new RevisionVersionAnterior(2, revisionEn(EstadoRevisionAsesor.PENDIENTE));

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaRevisionMasRecienteEstaCerrada() {
        // Arrange
        var regla = new ArtefactoNoCerradoRuleImpl();
        var entrada = new RevisionVersionAnterior(2, revisionEn(EstadoRevisionAsesor.CERRADA));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(ArtefactoCerradoException.class,
                        ex -> assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.Artefacto.CERRADO));
    }

    @Test
    void noDebeLanzar_cuandoNoHayVersionesEnArtefactoNoCerrado() {
        // Arrange
        var regla = new ArtefactoNoCerradoRuleImpl();
        var entrada = new RevisionVersionAnterior(0, RevisionAsesorDomain.VACIO);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoRevisionAsesor.class, names = {"PENDIENTE", "EN_PROGRESO", "RESUELTA"})
    void noDebeLanzar_cuandoLaRevisionNoEstaCerrada(EstadoRevisionAsesor estado) {
        // Arrange
        var regla = new ArtefactoNoCerradoRuleImpl();
        var entrada = new RevisionVersionAnterior(2, revisionEn(estado));

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoRevisionAsesor.class, names = {"PENDIENTE", "RESUELTA"})
    void debeLanzar_cuandoLaRevisionNoEstaEnProgreso(EstadoRevisionAsesor estado) {
        // Arrange
        var regla = new RevisionVersionAnteriorEnProgresoRuleImpl();
        var entrada = new RevisionVersionAnterior(2, revisionEn(estado));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionVersionAnteriorNoEnProgresoException.class,
                        ex -> assertThat(ex.getCodigoError())
                                .isEqualTo(ArtefactosCodes.RevisionAsesor.VERSION_ANTERIOR_NO_EN_PROGRESO));
    }

    @Test
    void noDebeLanzar_cuandoLaRevisionEstaEnProgreso() {
        // Arrange
        var regla = new RevisionVersionAnteriorEnProgresoRuleImpl();
        var entrada = new RevisionVersionAnterior(2, revisionEn(EstadoRevisionAsesor.EN_PROGRESO));

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoNoHayVersionesEnRevisionEnProgreso() {
        // Arrange
        var regla = new RevisionVersionAnteriorEnProgresoRuleImpl();
        var entrada = new RevisionVersionAnterior(0, RevisionAsesorDomain.VACIO);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
