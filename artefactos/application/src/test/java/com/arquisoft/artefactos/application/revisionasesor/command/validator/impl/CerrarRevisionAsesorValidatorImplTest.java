package com.arquisoft.artefactos.application.revisionasesor.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoOmitidoException;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.artefactos.domain.revisionasesor.CierreRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.ObservacionesVersionAnteriorSinCerrarException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorConObservacionesException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoCerrableException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoEncontradaException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ObservacionesParaCierre;
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

class CerrarRevisionAsesorValidatorImplTest {

    private static final ObservacionesParaCierre LIMPIAS = new ObservacionesParaCierre(false, false);

    private final CerrarRevisionAsesorValidatorImpl validator = new CerrarRevisionAsesorValidatorImpl();

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
    private final CierreRevisionAsesorDomain cierre = CierreRevisionAsesorDomain.crear(revisionId, asesorId);

    private RevisionAsesorDomain revision(EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(revisionId, versionId, estado, UtilFecha.generarInstanteActual());
    }

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatCode(() -> validator.validar(
                cierre, revision, artefacto, asesor, version, versionRepositorio, LIMPIAS))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaRevisionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cierre, RevisionAsesorDomain.VACIO, artefacto, asesor, version, versionRepositorio, LIMPIAS))
                .isInstanceOf(RevisionAsesorNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoElArtefactoNoExiste() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cierre, revision, ArtefactoDomain.VACIO, asesor, version, versionRepositorio, LIMPIAS))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoElAsesorNoExiste() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cierre, revision, artefacto, AsesorDomain.VACIO, version, versionRepositorio, LIMPIAS))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoLaVersionNoExiste() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cierre, revision, artefacto, asesor, VersionArtefactoDomain.VACIO, versionRepositorio, LIMPIAS))
                .isInstanceOf(VersionArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoLaVersionDelRepositorioNoExiste() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cierre, revision, artefacto, asesor, version, VersionRepositorioArtefactoDomain.VACIO, LIMPIAS))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoElLlamanteNoEsElAsesorDelArtefacto() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);
        var otroAsesor = UtilUUID.generarNuevoUUID();
        var cierreAjeno = CierreRevisionAsesorDomain.crear(revisionId, otroAsesor);
        var asesorAjeno = AsesorDomain.reconstruir(
                otroAsesor, "20161020124", "Otro", "otro@uco.edu.co", UtilFecha.generarInstanteActual(), null);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cierreAjeno, revision, artefacto, asesorAjeno, version, versionRepositorio, LIMPIAS))
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
                cierre, revision, omitido, asesor, version, versionRepositorio, LIMPIAS))
                .isInstanceOf(ArtefactoOmitidoException.class);
    }

    @Test
    void debeLanzar_cuandoLaRevisionNoEstaPendiente() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cierre, revision, artefacto, asesor, version, versionRepositorio, LIMPIAS))
                .isInstanceOf(RevisionAsesorNoCerrableException.class);
    }

    @Test
    void debeLanzar_cuandoLaRevisionTieneObservacionesPropias() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cierre, revision, artefacto, asesor, version,
                versionRepositorio, new ObservacionesParaCierre(true, false)))
                .isInstanceOf(RevisionAsesorConObservacionesException.class);
    }

    @Test
    void debeLanzar_cuandoLaVersionAnteriorTieneObservacionesSinCerrar() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cierre, revision, artefacto, asesor, version,
                versionRepositorio, new ObservacionesParaCierre(false, true)))
                .isInstanceOf(ObservacionesVersionAnteriorSinCerrarException.class);
    }

    @Test
    void debePriorizarExistenciaDeRevision_cuandoTodoEstaVacio() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cierre, RevisionAsesorDomain.VACIO, ArtefactoDomain.VACIO,
                AsesorDomain.VACIO, VersionArtefactoDomain.VACIO, VersionRepositorioArtefactoDomain.VACIO,
                new ObservacionesParaCierre(true, true)))
                .isInstanceOf(RevisionAsesorNoEncontradaException.class);
    }

    @Test
    void debePriorizarEstadoSobreObservaciones_cuandoAmbosFallan() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.RESUELTA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cierre, revision, artefacto, asesor, version,
                versionRepositorio, new ObservacionesParaCierre(true, true)))
                .isInstanceOf(RevisionAsesorNoCerrableException.class);
    }

    @Test
    void debePriorizarObservacionesPropiasSobreVersionAnterior_cuandoAmbasFallan() {
        // Arrange
        var revision = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cierre, revision, artefacto, asesor, version,
                versionRepositorio, new ObservacionesParaCierre(true, true)))
                .isInstanceOf(RevisionAsesorConObservacionesException.class);
    }
}
