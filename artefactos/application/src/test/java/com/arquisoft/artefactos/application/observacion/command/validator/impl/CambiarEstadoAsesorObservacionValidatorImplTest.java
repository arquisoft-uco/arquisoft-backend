package com.arquisoft.artefactos.application.observacion.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoOmitidoException;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.artefactos.domain.observacion.CambioEstadoAsesorObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.exception.EstadoDestinoObservacionNoPermitidoException;
import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoEncontradaException;
import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoSolucionadaException;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoAdmiteDecisionObservacionException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoEncontradaException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
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

class CambiarEstadoAsesorObservacionValidatorImplTest {

    private final CambiarEstadoAsesorObservacionValidatorImpl validator =
            new CambiarEstadoAsesorObservacionValidatorImpl();

    private final UUID observacionId = UtilUUID.generarNuevoUUID();
    private final UUID revisionId = UtilUUID.generarNuevoUUID();
    private final UUID asesorId = UtilUUID.generarNuevoUUID();
    private final UUID versionId = UtilUUID.generarNuevoUUID();
    private final UUID versionRepositorioId = UtilUUID.generarNuevoUUID();
    private final ObservacionDomain observacion = observacion(EstadoObservacion.SOLUCIONADA);
    private final RevisionAsesorDomain revision = revision(EstadoRevisionAsesor.EN_PROGRESO);
    private final ArtefactoDomain artefacto = ArtefactoDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), versionRepositorioId, asesorId, null);
    private final AsesorDomain asesor = AsesorDomain.reconstruir(
            asesorId, "20161020123", "Ana", "ana@uco.edu.co", UtilFecha.generarInstanteActual(), null);
    private final VersionArtefactoDomain version = VersionArtefactoDomain.reconstruir(
            versionId, artefacto.getId(), 1, UtilFecha.generarInstanteActual(), "uri");
    private final VersionRepositorioArtefactoDomain versionRepositorio =
            VersionRepositorioArtefactoDomain.reconstruir(versionRepositorioId, "Anteproyecto", 1,
                    UtilUUID.generarNuevoUUID(), "Repo", new DatosArchivo("a.pdf", "application/pdf", 10L));
    private final CambioEstadoAsesorObservacionDomain cambio = cambio(asesorId, "REABIERTA");

    private ObservacionDomain observacion(EstadoObservacion estado) {
        return ObservacionDomain.reconstruir(observacionId, revisionId, "Texto", estado);
    }

    private RevisionAsesorDomain revision(EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(revisionId, versionId, estado, UtilFecha.generarInstanteActual());
    }

    private CambioEstadoAsesorObservacionDomain cambio(UUID asesor, String estado) {
        return CambioEstadoAsesorObservacionDomain.crear(observacionId, estado, asesor);
    }

    @Test
    void debePasar_cuandoTodoEsValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(
                cambio, observacion, revision, artefacto, asesor, version, versionRepositorio))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaObservacionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, ObservacionDomain.VACIO, revision, artefacto, asesor, version, versionRepositorio))
                .isInstanceOf(ObservacionNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoLaRevisionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, RevisionAsesorDomain.VACIO, artefacto, asesor, version, versionRepositorio))
                .isInstanceOf(RevisionAsesorNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoElArtefactoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, revision, ArtefactoDomain.VACIO, asesor, version, versionRepositorio))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoElAsesorNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, revision, artefacto, AsesorDomain.VACIO, version, versionRepositorio))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoLaVersionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, revision, artefacto, asesor, VersionArtefactoDomain.VACIO, versionRepositorio))
                .isInstanceOf(VersionArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoElDocumentoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, revision, artefacto, asesor, version, VersionRepositorioArtefactoDomain.VACIO))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoElAsesorNoEsElDelArtefacto() {
        // Arrange
        var otroAsesor = UtilUUID.generarNuevoUUID();
        var cambioAjeno = cambio(otroAsesor, "REABIERTA");
        var asesorAjeno = AsesorDomain.reconstruir(
                otroAsesor, "20161020124", "Otro", "otro@uco.edu.co", UtilFecha.generarInstanteActual(), null);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambioAjeno, observacion, revision, artefacto, asesorAjeno, version, versionRepositorio))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzar_cuandoElArtefactoEstaOmitido() {
        // Arrange
        var omitido = ArtefactoDomain.reconstruir(artefacto.getId(), artefacto.getProyectoGrado(),
                versionRepositorioId, asesorId, UtilFecha.generarInstanteActual());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, revision, omitido, asesor, version, versionRepositorio))
                .isInstanceOf(ArtefactoOmitidoException.class);
    }

    @Test
    void debeLanzar_cuandoLaRevisionEstaPendiente() {
        // Arrange
        var pendiente = revision(EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, pendiente, artefacto, asesor, version, versionRepositorio))
                .isInstanceOf(RevisionAsesorNoAdmiteDecisionObservacionException.class);
    }

    @Test
    void debeLanzar_cuandoLaObservacionNoEstaSolucionada() {
        // Arrange
        var cerrada = observacion(EstadoObservacion.CERRADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, cerrada, revision, artefacto, asesor, version, versionRepositorio))
                .isInstanceOf(ObservacionNoSolucionadaException.class);
    }

    @Test
    void debeLanzar_cuandoElDestinoNoEstaPermitido() {
        // Arrange
        var cambioInvalido = cambio(asesorId, "EN_PROGRESO");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambioInvalido, observacion, revision, artefacto, asesor, version, versionRepositorio))
                .isInstanceOf(EstadoDestinoObservacionNoPermitidoException.class);
    }

    @Test
    void debePriorizarExistenciaDeObservacion_cuandoFaltanVariasCosas() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, ObservacionDomain.VACIO, RevisionAsesorDomain.VACIO,
                ArtefactoDomain.VACIO, AsesorDomain.VACIO, VersionArtefactoDomain.VACIO,
                VersionRepositorioArtefactoDomain.VACIO))
                .isInstanceOf(ObservacionNoEncontradaException.class);
    }
}
