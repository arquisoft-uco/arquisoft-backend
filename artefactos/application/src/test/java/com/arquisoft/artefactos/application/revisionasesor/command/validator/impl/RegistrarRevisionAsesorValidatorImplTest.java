package com.arquisoft.artefactos.application.revisionasesor.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoOmitidoException;
import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.revisionasesor.RegistroRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorAbiertaExistenteException;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionVersionAnteriorNoResueltaException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEncontradaException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEsUltimaException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarRevisionAsesorValidatorImplTest {

    private final RegistrarRevisionAsesorValidatorImpl validator = new RegistrarRevisionAsesorValidatorImpl();

    private UUID asesorId;
    private RegistroRevisionAsesorDomain registro;
    private VersionArtefactoDomain version;
    private ArtefactoDomain artefacto;
    private RevisionAsesorDomain revisionAnterior;

    @BeforeEach
    void setUp() {
        asesorId = UtilUUID.generarNuevoUUID();
        var versionId = UtilUUID.generarNuevoUUID();
        var artefactoId = UtilUUID.generarNuevoUUID();
        registro = RegistroRevisionAsesorDomain.crear(RevisionAsesorDomain.crear(versionId), asesorId);
        version = VersionArtefactoDomain.reconstruir(versionId, artefactoId, 3, Instant.now(), "versiones/uri", 1024L);
        artefacto = ArtefactoDomain.reconstruir(artefactoId, UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), asesorId, null);
        revisionAnterior = revision(EstadoRevisionAsesor.RESUELTA);
    }

    private static RevisionAsesorDomain revision(EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                estado, Instant.now());
    }

    @Test
    void noDebeLanzar_cuandoTodoEsValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(registro, version, artefacto, true, false, revisionAnterior))
                .doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoEsLaPrimeraVersion() {
        // Arrange
        var primera = VersionArtefactoDomain.reconstruir(version.getId(), artefacto.getId(), 1, Instant.now(),
                "versiones/uri", 1024L);

        // Act & Assert
        assertThatCode(() -> validator.validar(registro, primera, artefacto, true, false,
                RevisionAsesorDomain.VACIO)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaVersionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, VersionArtefactoDomain.VACIO, artefacto, true, false,
                revisionAnterior))
                .isInstanceOf(VersionArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzar_cuandoElArtefactoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, version, ArtefactoDomain.VACIO, true, false,
                revisionAnterior))
                .isInstanceOf(ArtefactoNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoElAsesorNoEsElDelArtefacto() {
        // Arrange
        var ajeno = RegistroRevisionAsesorDomain.crear(
                RevisionAsesorDomain.crear(version.getId()), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(ajeno, version, artefacto, true, false, revisionAnterior))
                .isInstanceOf(AsesorNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzarArtefactoOmitido_antesQueLaVersionNoEsUltima_cuandoElArtefactoEstaOmitido() {
        // Arrange
        var omitido = ArtefactoDomain.reconstruir(artefacto.getId(), artefacto.getProyectoGrado(),
                artefacto.getVersionRepositorioArtefacto(), asesorId, Instant.now());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, version, omitido, false, true, revisionAnterior))
                .isInstanceOf(ArtefactoOmitidoException.class);
    }

    @Test
    void debeLanzar_cuandoLaVersionNoEsLaUltima() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, version, artefacto, false, false, revisionAnterior))
                .isInstanceOf(VersionArtefactoNoEsUltimaException.class);
    }

    @Test
    void debeLanzar_cuandoYaExisteRevisionAbierta() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, version, artefacto, true, true, revisionAnterior))
                .isInstanceOf(RevisionAsesorAbiertaExistenteException.class);
    }

    @Test
    void debeValidarExistenciaAntesQuePropiedad_cuandoFallanTodasLasReglas() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                registro, VersionArtefactoDomain.VACIO, ArtefactoDomain.VACIO, false, true, revisionAnterior))
                .isInstanceOf(VersionArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoEsUltima_antesQueRevisionAbierta() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, version, artefacto, false, true, revisionAnterior))
                .isInstanceOf(VersionArtefactoNoEsUltimaException.class);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoRevisionAsesor.class, names = {"PENDIENTE", "EN_PROGRESO"})
    void debeLanzar_cuandoLaRevisionAnteriorNoEstaResuelta(EstadoRevisionAsesor estado) {
        // Arrange
        var sinResolver = revision(estado);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, version, artefacto, true, false, sinResolver))
                .isInstanceOf(RevisionVersionAnteriorNoResueltaException.class);
    }

    @Test
    void debeLanzarRevisionAbiertaExistente_antesQueVersionAnteriorNoResuelta() {
        // Arrange
        var sinResolver = revision(EstadoRevisionAsesor.EN_PROGRESO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(registro, version, artefacto, true, true, sinResolver))
                .isInstanceOf(RevisionAsesorAbiertaExistenteException.class);
    }
}
