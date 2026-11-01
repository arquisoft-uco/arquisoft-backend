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
import com.arquisoft.artefactos.domain.observacion.exception.ObservacionDuplicadaException;
import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoEncontradaException;
import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoReabribleException;
import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoSolucionadaException;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.model.VersionesDeObservacion;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoAdmiteDecisionObservacionException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoEncontradaException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionVersionSiguienteEnviadaException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionVersionSiguienteNoRegistradaException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEncontradaException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

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
    private final RevisionAsesorDomain revision = revision(EstadoRevisionAsesor.RESUELTA);
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
        return ObservacionDomain.reconstruir(observacionId, revisionId, "Texto", estado,
                UtilUUID.obtenerUUIDPorDefecto());
    }

    private VersionesDeObservacion versiones(VersionArtefactoDomain version) {
        return new VersionesDeObservacion(version, 2, revisionSiguiente(EstadoRevisionAsesor.PENDIENTE), false);
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
                cambio, observacion, revision, artefacto, asesor, versionRepositorio, versiones(version)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaObservacionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, ObservacionDomain.VACIO, revision, artefacto, asesor, versionRepositorio, versiones(version)))
                .isInstanceOf(ObservacionNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoLaRevisionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, RevisionAsesorDomain.VACIO, artefacto, asesor, versionRepositorio, versiones(version)))
                .isInstanceOf(RevisionAsesorNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoElArtefactoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, revision, ArtefactoDomain.VACIO, asesor, versionRepositorio, versiones(version)))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoElAsesorNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, revision, artefacto, AsesorDomain.VACIO, versionRepositorio, versiones(version)))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoLaVersionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, revision, artefacto, asesor, versionRepositorio, versiones(VersionArtefactoDomain.VACIO)))
                .isInstanceOf(VersionArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoElDocumentoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, revision, artefacto, asesor, VersionRepositorioArtefactoDomain.VACIO, versiones(version)))
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
                cambioAjeno, observacion, revision, artefacto, asesorAjeno, versionRepositorio, versiones(version)))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzar_cuandoElArtefactoEstaOmitido() {
        // Arrange
        var omitido = ArtefactoDomain.reconstruir(artefacto.getId(), artefacto.getProyectoGrado(),
                versionRepositorioId, asesorId, UtilFecha.generarInstanteActual());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, revision, omitido, asesor, versionRepositorio, versiones(version)))
                .isInstanceOf(ArtefactoOmitidoException.class);
    }

    @ParameterizedTest
    @CsvSource({"PENDIENTE,CERRADA", "PENDIENTE,REABIERTA", "EN_PROGRESO,CERRADA", "EN_PROGRESO,REABIERTA",
            "CERRADA,CERRADA", "CERRADA,REABIERTA"})
    void debeLanzar_cuandoLaRevisionNoEstaResuelta(EstadoRevisionAsesor estado, String destino) {
        // Arrange
        var cambioDestino = cambio(asesorId, destino);
        var noResuelta = revision(estado);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambioDestino, observacion, noResuelta, artefacto, asesor, versionRepositorio, versiones(version)))
                .isInstanceOf(RevisionAsesorNoAdmiteDecisionObservacionException.class);
    }

    @Test
    void debeLanzar_cuandoLaObservacionNoEstaSolucionada() {
        // Arrange
        var cerrada = observacion(EstadoObservacion.CERRADA);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, cerrada, revision, artefacto, asesor, versionRepositorio, versiones(version)))
                .isInstanceOf(ObservacionNoSolucionadaException.class);
    }

    @Test
    void debeLanzar_cuandoElDestinoNoEstaPermitido() {
        // Arrange
        var cambioInvalido = cambio(asesorId, "EN_PROGRESO");

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambioInvalido, observacion, revision, artefacto, asesor, versionRepositorio, versiones(version)))
                .isInstanceOf(EstadoDestinoObservacionNoPermitidoException.class);
    }

    private VersionesDeObservacion versiones(int ultima, RevisionAsesorDomain siguiente, boolean repetida) {
        return new VersionesDeObservacion(version, ultima, siguiente, repetida);
    }

    private RevisionAsesorDomain revisionSiguiente(EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), estado, UtilFecha.generarInstanteActual());
    }

    @Test
    void debePasar_cuandoReabreConContinuacionEnRevisionSiguientePendiente() {
        // Arrange
        var versiones = versiones(2, revisionSiguiente(EstadoRevisionAsesor.PENDIENTE), false);

        // Act & Assert
        assertThatCode(() -> validator.validar(
                cambio, observacion, revision(EstadoRevisionAsesor.RESUELTA), artefacto, asesor, versionRepositorio,
                versiones)).doesNotThrowAnyException();
    }

    @Test
    void debePermitirCerrar_fueraDeLaVentana() {
        // Arrange
        var cierre = cambio(asesorId, "CERRADA");
        var versiones = versiones(5, RevisionAsesorDomain.VACIO, true);

        // Act & Assert
        assertThatCode(() -> validator.validar(
                cierre, observacion, revision(EstadoRevisionAsesor.RESUELTA), artefacto, asesor, versionRepositorio,
                versiones)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 4})
    void debeLanzar_cuandoLaObservacionNoEsReabrible(int ultima) {
        // Arrange
        var versiones = versiones(ultima, RevisionAsesorDomain.VACIO, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, revision, artefacto, asesor, versionRepositorio, versiones))
                .isInstanceOf(ObservacionNoReabribleException.class);
    }

    @Test
    void debeLanzar_cuandoLaRevisionDeLaVersionSiguienteNoEstaRegistrada() {
        // Arrange
        var versiones = versiones(2, RevisionAsesorDomain.VACIO, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, revision, artefacto, asesor, versionRepositorio, versiones))
                .isInstanceOf(RevisionVersionSiguienteNoRegistradaException.class);
    }

    @Test
    void debeLanzar_cuandoLaRevisionDeLaVersionSiguienteYaSeEnvio() {
        // Arrange
        var versiones = versiones(2, revisionSiguiente(EstadoRevisionAsesor.EN_PROGRESO), false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, revision, artefacto, asesor, versionRepositorio, versiones))
                .isInstanceOf(RevisionVersionSiguienteEnviadaException.class);
    }

    @Test
    void debeLanzar_cuandoLaDescripcionYaExisteEnLaRevisionSiguiente() {
        // Arrange
        var versiones = versiones(2, revisionSiguiente(EstadoRevisionAsesor.PENDIENTE), true);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                cambio, observacion, revision, artefacto, asesor, versionRepositorio, versiones))
                .isInstanceOf(ObservacionDuplicadaException.class);
    }

    @Test
    void debePriorizarExistenciaDeObservacion_cuandoFaltanVariasCosas() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, ObservacionDomain.VACIO, RevisionAsesorDomain.VACIO,
                ArtefactoDomain.VACIO, AsesorDomain.VACIO, VersionRepositorioArtefactoDomain.VACIO,
                versiones(VersionArtefactoDomain.VACIO)))
                .isInstanceOf(ObservacionNoEncontradaException.class);
    }
}
