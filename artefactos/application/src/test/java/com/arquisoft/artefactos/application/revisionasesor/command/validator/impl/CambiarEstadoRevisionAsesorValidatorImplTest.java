package com.arquisoft.artefactos.application.revisionasesor.command.validator.impl;

import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorDeRevisionNoEncontradoException;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.artefactos.domain.revisionasesor.CambioEstadoRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.EstadoDestinoRevisionAsesorNoPermitidoException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.EstudianteNoParticipaArtefactoException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorCerradaException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoEncontradaException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorPendienteException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.VersionPosteriorARevisionNoExisteException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.time.Instant;

class CambiarEstadoRevisionAsesorValidatorImplTest {

    private final CambiarEstadoRevisionAsesorValidatorImpl validator = new CambiarEstadoRevisionAsesorValidatorImpl();

    private final UUID revisionId = UtilUUID.generarNuevoUUID();
    private final UUID estudianteId = UtilUUID.generarNuevoUUID();
    private final AsesorDomain asesor = AsesorDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), "20161020123", "Ana", "ana@uco.edu.co", Instant.now(), null);
    private final EstudianteDomain estudiante = EstudianteDomain.reconstruir(
            estudianteId, "20201020123", "Luis", "luis@uco.edu.co", Instant.now(), null);

    private RevisionAsesorDomain revision(EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(
                revisionId, UtilUUID.generarNuevoUUID(), estado, Instant.now());
    }

    private CambioEstadoRevisionAsesorDomain cambio(String estado) {
        return CambioEstadoRevisionAsesorDomain.crear(revisionId, estado, estudianteId);
    }

    @Test
    void noDebeLanzar_cuandoTodoEsValido() {
        // Arrange
        var cambio = cambio("RESUELTA");
        var revision = revision(EstadoRevisionAsesor.EN_PROGRESO);

        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, revision, asesor, estudiante, true, true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaRevisionNoExiste() {
        // Arrange
        var cambio = cambio("RESUELTA");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, RevisionAsesorDomain.VACIO, asesor, estudiante, true, true))
                .isInstanceOf(RevisionAsesorNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoElEstudianteNoExiste() {
        // Arrange
        var cambio = cambio("RESUELTA");
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, revision, asesor, EstudianteDomain.VACIO, true, true))
                .isInstanceOf(EstudianteNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoElAsesorNoExiste() {
        // Arrange
        var cambio = cambio("RESUELTA");
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, revision, AsesorDomain.VACIO, estudiante, true, true))
                .isInstanceOf(AsesorDeRevisionNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoElEstudianteNoParticipa() {
        // Arrange
        var cambio = cambio("RESUELTA");
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, revision, asesor, estudiante, false, true))
                .isInstanceOf(EstudianteNoParticipaArtefactoException.class);
    }

    @Test
    void debeLanzar_cuandoLaRevisionEstaCerrada() {
        // Arrange
        var cambio = cambio("RESUELTA");
        var revision = revision(EstadoRevisionAsesor.CERRADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, revision, asesor, estudiante, true, true))
                .isInstanceOf(RevisionAsesorCerradaException.class);
    }

    @Test
    void debeLanzar_cuandoElDestinoEsCerrada() {
        // Arrange
        var cambio = cambio("CERRADA");
        var revision = revision(EstadoRevisionAsesor.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, revision, asesor, estudiante, true, true))
                .isInstanceOf(EstadoDestinoRevisionAsesorNoPermitidoException.class);
    }

    @Test
    void debeLanzar_cuandoElDestinoEsEnProgreso() {
        // Arrange
        var cambio = cambio("EN_PROGRESO");
        var revision = revision(EstadoRevisionAsesor.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, revision, asesor, estudiante, true, true))
                .isInstanceOf(EstadoDestinoRevisionAsesorNoPermitidoException.class);
    }

    @Test
    void debeLanzar_cuandoLaRevisionEstaPendiente() {
        // Arrange
        var cambio = cambio("RESUELTA");
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, revision, asesor, estudiante, true, true))
                .isInstanceOf(RevisionAsesorPendienteException.class);
    }

    @Test
    void debeLanzar_cuandoNoHayVersionPosterior() {
        // Arrange
        var cambio = cambio("RESUELTA");
        var revision = revision(EstadoRevisionAsesor.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, revision, asesor, estudiante, true, false))
                .isInstanceOf(VersionPosteriorARevisionNoExisteException.class);
    }

    @Test
    void debePriorizarPendiente_cuandoElDestinoTampocoEsPermitido() {
        // Arrange
        var cambio = cambio("EN_PROGRESO");
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, revision, asesor, estudiante, true, false))
                .isInstanceOf(RevisionAsesorPendienteException.class);
    }

    @Test
    void debePriorizarDestinoNoPermitido_cuandoTampocoHayVersionPosterior() {
        // Arrange
        var cambio = cambio("EN_PROGRESO");
        var revision = revision(EstadoRevisionAsesor.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, revision, asesor, estudiante, true, false))
                .isInstanceOf(EstadoDestinoRevisionAsesorNoPermitidoException.class);
    }

    @Test
    void debePriorizarRevisionInexistente_cuandoTambienFallanLasDemasReglas() {
        // Arrange
        var cambio = cambio("CERRADA");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, RevisionAsesorDomain.VACIO, AsesorDomain.VACIO, EstudianteDomain.VACIO, false, true))
                .isInstanceOf(RevisionAsesorNoEncontradaException.class);
    }

    @Test
    void debePriorizarRevisionCerrada_cuandoElDestinoTambienEsCerrada() {
        // Arrange
        var cambio = cambio("CERRADA");
        var revision = revision(EstadoRevisionAsesor.CERRADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, revision, asesor, estudiante, true, true))
                .isInstanceOf(RevisionAsesorCerradaException.class);
    }
}
