package com.arquisoft.artefactos.domain.revisionasesor.rules;

import com.arquisoft.artefactos.domain.revisionasesor.exception.EstadoDestinoRevisionAsesorNoPermitidoException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.EstudianteNoParticipaArtefactoException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorAbiertaExistenteException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorCerradaException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoEncontradaException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorPendienteException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.VersionPosteriorARevisionNoExisteException;
import com.arquisoft.artefactos.domain.revisionasesor.model.DestinoCambioEstado;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ExistenciaRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ParticipacionEstudianteArtefacto;
import com.arquisoft.artefactos.domain.revisionasesor.model.UnicidadRevisionAbiertaVersion;
import com.arquisoft.artefactos.domain.revisionasesor.model.VersionPosteriorRevision;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.EstadoDestinoRevisionAsesorPermitidoRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.EstudianteParticipaArtefactoRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorExisteRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorNoCerradaRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorNoPendienteRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorUnicaAbiertaPorVersionRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.VersionPosteriorARevisionExisteRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionAsesorRulesTest {

    @Test
    void debeLanzar_cuandoLaRevisionNoExiste() {
        // Arrange
        var regla = new RevisionAsesorExisteRuleImpl();
        var entrada = new ExistenciaRevisionAsesor(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionAsesorNoEncontradaException.class,
                        ex -> assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.NO_ENCONTRADA));
    }

    @Test
    void noDebeLanzar_cuandoLaRevisionExiste() {
        // Arrange
        var regla = new RevisionAsesorExisteRuleImpl();
        var entrada = new ExistenciaRevisionAsesor(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoElEstudianteNoParticipa() {
        // Arrange
        var regla = new EstudianteParticipaArtefactoRuleImpl();
        var entrada = new ParticipacionEstudianteArtefacto(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(EstudianteNoParticipaArtefactoException.class,
                        ex -> assertThat(ex.getCodigoError())
                                .isEqualTo(ArtefactosCodes.RevisionAsesor.ESTUDIANTE_NO_PARTICIPA_ARTEFACTO));
    }

    @Test
    void noDebeLanzar_cuandoElEstudianteParticipa() {
        // Arrange
        var regla = new EstudianteParticipaArtefactoRuleImpl();
        var entrada = new ParticipacionEstudianteArtefacto(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaRevisionEstaCerrada() {
        // Arrange
        var regla = new RevisionAsesorNoCerradaRuleImpl();
        var entrada = new EstadoActualRevisionAsesor(UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.CERRADA);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionAsesorCerradaException.class,
                        ex -> assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.CERRADA));
    }

    @Test
    void noDebeLanzar_cuandoLaRevisionNoEstaCerrada() {
        // Arrange
        var regla = new RevisionAsesorNoCerradaRuleImpl();
        var entrada = new EstadoActualRevisionAsesor(UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.RESUELTA);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoRevisionAsesor.class, names = {"PENDIENTE", "EN_PROGRESO", "CERRADA"})
    void debeLanzar_cuandoElDestinoNoEsResuelta(EstadoRevisionAsesor destino) {
        // Arrange
        var regla = new EstadoDestinoRevisionAsesorPermitidoRuleImpl();
        var entrada = new DestinoCambioEstado(UtilUUID.generarNuevoUUID(), destino);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(EstadoDestinoRevisionAsesorNoPermitidoException.class,
                        ex -> assertThat(ex.getCodigoError())
                                .isEqualTo(ArtefactosCodes.RevisionAsesor.ESTADO_DESTINO_NO_PERMITIDO));
    }

    @Test
    void noDebeLanzar_cuandoElDestinoEsResuelta() {
        // Arrange
        var regla = new EstadoDestinoRevisionAsesorPermitidoRuleImpl();
        var entrada = new DestinoCambioEstado(UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.RESUELTA);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaRevisionEstaPendiente() {
        // Arrange
        var regla = new RevisionAsesorNoPendienteRuleImpl();
        var entrada = new EstadoActualRevisionAsesor(UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionAsesorPendienteException.class,
                        ex -> assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.PENDIENTE));
    }

    @Test
    void noDebeLanzar_cuandoLaRevisionYaFueEnviada() {
        // Arrange
        var regla = new RevisionAsesorNoPendienteRuleImpl();
        var entrada = new EstadoActualRevisionAsesor(UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.EN_PROGRESO);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoNoExisteVersionPosterior() {
        // Arrange
        var regla = new VersionPosteriorARevisionExisteRuleImpl();
        var entrada = new VersionPosteriorRevision(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(VersionPosteriorARevisionNoExisteException.class,
                        ex -> assertThat(ex.getCodigoError())
                                .isEqualTo(ArtefactosCodes.RevisionAsesor.SIN_VERSION_POSTERIOR));
    }

    @Test
    void noDebeLanzar_cuandoExisteVersionPosterior() {
        // Arrange
        var regla = new VersionPosteriorARevisionExisteRuleImpl();
        var entrada = new VersionPosteriorRevision(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoYaExisteRevisionAbiertaEnLaVersion() {
        // Arrange
        var regla = new RevisionAsesorUnicaAbiertaPorVersionRuleImpl();
        var entrada = new UnicidadRevisionAbiertaVersion(UtilUUID.generarNuevoUUID(), 3, true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionAsesorAbiertaExistenteException.class,
                        ex -> assertThat(ex.getCodigoError())
                                .isEqualTo(ArtefactosCodes.RevisionAsesor.ABIERTA_EXISTENTE));
    }

    @Test
    void noDebeLanzar_cuandoSoloHayRevisionesCerradasEnLaVersion() {
        // Arrange
        var regla = new RevisionAsesorUnicaAbiertaPorVersionRuleImpl();
        var entrada = new UnicidadRevisionAbiertaVersion(UtilUUID.generarNuevoUUID(), 3, false);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
