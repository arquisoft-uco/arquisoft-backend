package com.arquisoft.artefactos.application.versionartefacto.command.validator.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoOmitidoException;
import com.arquisoft.artefactos.domain.artefacto.exception.EstudianteNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.RevisionAnteriorNoPermiteEliminarVersionException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.versionartefacto.EliminacionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoConRevisionException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEncontradaException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEsUltimaParaEliminarException;
import com.arquisoft.artefactos.domain.versionartefacto.model.PrecondicionesEliminacionVersion;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EliminarVersionArtefactoValidatorImplTest {

    private final EliminarVersionArtefactoValidatorImpl validator = new EliminarVersionArtefactoValidatorImpl();

    private EliminacionVersionArtefactoDomain eliminacion;
    private VersionArtefactoDomain version;
    private ArtefactoDomain artefacto;

    @BeforeEach
    void setUp() {
        var versionId = UtilUUID.generarNuevoUUID();
        var artefactoId = UtilUUID.generarNuevoUUID();
        eliminacion = EliminacionVersionArtefactoDomain.crear(versionId, UtilUUID.generarNuevoUUID());
        version = VersionArtefactoDomain.reconstruir(versionId, artefactoId, 2, Instant.now(), "versiones/x");
        artefacto = ArtefactoDomain.reconstruir(artefactoId, UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), null);
    }

    private static RevisionAsesorDomain revision(EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                estado, UtilFecha.generarInstanteActual());
    }

    private static PrecondicionesEliminacionVersion previas(VersionArtefactoDomain ver, ArtefactoDomain art,
                                                            RevisionAsesorDomain anterior, boolean participa,
                                                            boolean esUltima, boolean tieneRevision) {
        return new PrecondicionesEliminacionVersion(ver, art, anterior, participa, esUltima, tieneRevision);
    }

    @Test
    void debeNoLanzar_cuandoLaRevisionAnteriorEstaEnProgreso() {
        // Arrange
        var previas = previas(version, artefacto, revision(EstadoRevisionAsesor.EN_PROGRESO), true, true, false);

        // Act & Assert
        assertThatCode(() -> validator.validar(eliminacion, previas)).doesNotThrowAnyException();
    }

    @Test
    void debeNoLanzar_cuandoEsLaPrimeraVersion() {
        // Arrange
        var primera = VersionArtefactoDomain.reconstruir(
                version.getId(), version.getArtefacto(), 1, Instant.now(), "versiones/x");
        var previas = previas(primera, artefacto, RevisionAsesorDomain.VACIO, true, true, false);

        // Act & Assert
        assertThatCode(() -> validator.validar(eliminacion, previas)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoLaVersionEsVacia() {
        // Arrange
        var previas = previas(VersionArtefactoDomain.VACIO, ArtefactoDomain.VACIO,
                RevisionAsesorDomain.VACIO, false, false, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, previas))
                .isInstanceOf(VersionArtefactoNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoPropietario_cuandoNoParticipa() {
        // Arrange
        var previas = previas(version, artefacto, revision(EstadoRevisionAsesor.EN_PROGRESO), false, true, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, previas))
                .isInstanceOf(EstudianteNoPropietarioArtefactoException.class);
    }

    @Test
    void debeLanzarOmitido_cuandoElArtefactoEstaOmitido() {
        // Arrange
        var omitido = ArtefactoDomain.reconstruir(artefacto.getId(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), Instant.now());
        var previas = previas(version, omitido, revision(EstadoRevisionAsesor.EN_PROGRESO), true, true, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, previas))
                .isInstanceOf(ArtefactoOmitidoException.class);
    }

    @Test
    void debeLanzarNoEsUltima_cuandoNoEsUltimaYAunTieneRevision() {
        // Arrange
        var previas = previas(version, artefacto, revision(EstadoRevisionAsesor.CERRADA), true, false, true);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, previas))
                .isInstanceOf(VersionArtefactoNoEsUltimaParaEliminarException.class);
    }

    @Test
    void debeLanzarConRevision_cuandoTieneRevisionYLaAnteriorNoEstaEnProgreso() {
        // Arrange
        var previas = previas(version, artefacto, revision(EstadoRevisionAsesor.CERRADA), true, true, true);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, previas))
                .isInstanceOf(VersionArtefactoConRevisionException.class);
    }

    @Test
    void debeLanzarRevisionAnterior_cuandoNoEstaEnProgreso() {
        // Arrange
        var previas = previas(version, artefacto, revision(EstadoRevisionAsesor.CERRADA), true, true, false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(eliminacion, previas))
                .isInstanceOf(RevisionAnteriorNoPermiteEliminarVersionException.class);
    }
}
