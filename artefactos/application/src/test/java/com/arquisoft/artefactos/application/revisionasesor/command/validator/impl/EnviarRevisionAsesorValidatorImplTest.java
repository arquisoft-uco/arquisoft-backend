package com.arquisoft.artefactos.application.revisionasesor.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoOmitidoException;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.artefactos.domain.observacion.AvanceObservacionesRevisionDomain;
import com.arquisoft.artefactos.domain.revisionasesor.EnvioRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoEncontradaException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoEnviableException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorSinObservacionesException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ObservacionesDeRevision;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEncontradaException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnviarRevisionAsesorValidatorImplTest {

    private final EnviarRevisionAsesorValidatorImpl validator = new EnviarRevisionAsesorValidatorImpl();

    private final UUID revisionId = UtilUUID.generarNuevoUUID();
    private final UUID asesorId = UtilUUID.generarNuevoUUID();
    private final UUID versionId = UtilUUID.generarNuevoUUID();
    private final UUID versionRepositorioId = UtilUUID.generarNuevoUUID();
    private final ArtefactoDomain artefacto = ArtefactoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), versionRepositorioId, asesorId, null);
    private final AsesorDomain asesor = AsesorDomain.reconstruir(
            asesorId, "20161020123", "Ana", "ana@uco.edu.co", UtilFecha.generarInstanteActual(), null);
    private final VersionArtefactoDomain version = VersionArtefactoDomain.reconstruir(
            versionId, artefacto.getId(), 1, UtilFecha.generarInstanteActual(), "uri");
    private final VersionRepositorioArtefactoDomain versionRepositorio =
            VersionRepositorioArtefactoDomain.reconstruir(versionRepositorioId, "Anteproyecto", 1,
                    UtilUUID.generarNuevoUUID(), "Repo", new DatosArchivo("a.pdf", "application/pdf", 10L));
    private final EnvioRevisionAsesorDomain envio = EnvioRevisionAsesorDomain.crear(
            revisionId, asesorId, AvanceObservacionesRevisionDomain.crear(revisionId));

    private final ObservacionesDeRevision observaciones = new ObservacionesDeRevision(revisionId, true);

    private RevisionAsesorDomain revision(EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(revisionId, versionId, estado, UtilFecha.generarInstanteActual());
    }

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatCode(() -> validator.validar(
                envio, revision, artefacto, asesor, version, versionRepositorio, observaciones))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaRevisionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                envio, RevisionAsesorDomain.VACIO, artefacto, asesor, version, versionRepositorio, observaciones))
                .isInstanceOf(RevisionAsesorNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoElArtefactoNoExiste() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                envio, revision, ArtefactoDomain.VACIO, asesor, version, versionRepositorio, observaciones))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoElAsesorNoExiste() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                envio, revision, artefacto, AsesorDomain.VACIO, version, versionRepositorio, observaciones))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoLaVersionNoExiste() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                envio, revision, artefacto, asesor, VersionArtefactoDomain.VACIO, versionRepositorio, observaciones))
                .isInstanceOf(VersionArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoLaVersionDelRepositorioNoExiste() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                envio, revision, artefacto, asesor, version, VersionRepositorioArtefactoDomain.VACIO,
                observaciones))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoElLlamanteNoEsElAsesorDelArtefacto() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);
        var otroAsesor = UtilUUID.generarNuevoUUID();
        var envioAjeno = EnvioRevisionAsesorDomain.crear(
                revisionId, otroAsesor, AvanceObservacionesRevisionDomain.crear(revisionId));
        var asesorAjeno = AsesorDomain.reconstruir(
                otroAsesor, "20161020124", "Otro", "otro@uco.edu.co", UtilFecha.generarInstanteActual(), null);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                envioAjeno, revision, artefacto, asesorAjeno, version, versionRepositorio, observaciones))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzar_cuandoElArtefactoEstaOmitido() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);
        var omitido = ArtefactoDomain.reconstruir(artefacto.getId(), artefacto.getProyectoGrado(),
                versionRepositorioId, asesorId, UtilFecha.generarInstanteActual());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                envio, revision, omitido, asesor, version, versionRepositorio, observaciones))
                .isInstanceOf(ArtefactoOmitidoException.class);
    }

    @Test
    void debeLanzar_cuandoLaRevisionNoEstaPendiente() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                envio, revision, artefacto, asesor, version, versionRepositorio, observaciones))
                .isInstanceOf(RevisionAsesorNoEnviableException.class);
    }

    @Test
    void debePriorizarExistenciaDeRevision_cuandoTodoEstaVacio() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(envio, RevisionAsesorDomain.VACIO, ArtefactoDomain.VACIO,
                AsesorDomain.VACIO, VersionArtefactoDomain.VACIO, VersionRepositorioArtefactoDomain.VACIO,
                observaciones))
                .isInstanceOf(RevisionAsesorNoEncontradaException.class);
    }

    @Test
    void debePriorizarPropiedadSobreEstado_cuandoAmbosFallan() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.CERRADA);
        var otroAsesor = UtilUUID.generarNuevoUUID();
        var envioAjeno = EnvioRevisionAsesorDomain.crear(
                revisionId, otroAsesor, AvanceObservacionesRevisionDomain.crear(revisionId));
        var asesorAjeno = AsesorDomain.reconstruir(
                otroAsesor, "20161020124", "Otro", "otro@uco.edu.co", UtilFecha.generarInstanteActual(), null);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                envioAjeno, revision, artefacto, asesorAjeno, version, versionRepositorio, observaciones))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzar_cuandoLaRevisionNoTieneObservaciones() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);
        var sinObservaciones = new ObservacionesDeRevision(revisionId, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                envio, revision, artefacto, asesor, version, versionRepositorio, sinObservaciones))
                .isInstanceOf(RevisionAsesorSinObservacionesException.class);
    }

    @Test
    void debePriorizarEstadoSobreObservaciones_cuandoLaRevisionNoEstaPendienteYNoTieneObservaciones() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.EN_PROGRESO);
        var sinObservaciones = new ObservacionesDeRevision(revisionId, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                envio, revision, artefacto, asesor, version, versionRepositorio, sinObservaciones))
                .isInstanceOf(RevisionAsesorNoEnviableException.class);
    }
}
