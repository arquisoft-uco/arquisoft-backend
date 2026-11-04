package com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.observacion.support.ObservacionTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.RevisionAsesorTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.VersionArtefactoTestEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
class ObservacionCommandRepositoryTest {

    private static final String CERRADA = "CERRADA";

    @Autowired
    private ObservacionCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private ObservacionCommandOutputAdapter adapter;

    private UUID artefacto;

    @BeforeEach
    void setUp() {
        adapter = new ObservacionCommandOutputAdapter(repository, mock(AppLogger.class));
        artefacto = UtilUUID.generarNuevoUUID();
    }

    private UUID sembrarVersion(UUID artefactoId, int numero) {
        var version = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionArtefactoTestEntity(version, artefactoId, numero));
        return version;
    }

    private UUID sembrarRevision(UUID version, String estado) {
        var revision = UtilUUID.generarNuevoUUID();
        entityManager.persist(new RevisionAsesorTestEntity(revision, version, estado));
        return revision;
    }

    private void sembrarObservacion(UUID revision, String estado) {
        entityManager.persist(new ObservacionTestEntity(UtilUUID.generarNuevoUUID(), revision, "Detalle", estado));
    }

    @Test
    void debeDiscriminarPorRevision_cuandoSeConsultaSiExistenObservaciones() {
        // Arrange
        var conObservaciones = UtilUUID.generarNuevoUUID();
        sembrarObservacion(conObservaciones, "NUEVA");
        entityManager.flush();
        entityManager.clear();

        // Act
        var existen = adapter.existenPorRevisionAsesor(conObservaciones);
        var noExisten = adapter.existenPorRevisionAsesor(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(existen).isTrue();
        assertThat(noExisten).isFalse();
    }

    @Test
    void debeDevolverFalso_cuandoLaRevisionEsDeLaPrimeraVersion() {
        // Arrange
        var revision = sembrarRevision(sembrarVersion(artefacto, 1), "PENDIENTE");
        entityManager.flush();
        entityManager.clear();

        // Act
        var existen = adapter.existenNoCerradasDeVersionAnterior(revision, CERRADA);

        // Assert
        assertThat(existen).isFalse();
    }

    @Test
    void debeDevolverVerdadero_cuandoLaVersionAnteriorTieneObservacionNoCerrada() {
        // Arrange
        var revisionAnterior = sembrarRevision(sembrarVersion(artefacto, 1), "RESUELTA");
        sembrarObservacion(revisionAnterior, CERRADA);
        sembrarObservacion(revisionAnterior, "SOLUCIONADA");
        var revision = sembrarRevision(sembrarVersion(artefacto, 2), "PENDIENTE");
        entityManager.flush();
        entityManager.clear();

        // Act
        var existen = adapter.existenNoCerradasDeVersionAnterior(revision, CERRADA);

        // Assert
        assertThat(existen).isTrue();
    }

    @Test
    void debeDevolverFalso_cuandoTodasLasObservacionesDeLaVersionAnteriorEstanCerradas() {
        // Arrange
        var revisionAnterior = sembrarRevision(sembrarVersion(artefacto, 1), "CERRADA");
        sembrarObservacion(revisionAnterior, CERRADA);
        sembrarObservacion(revisionAnterior, CERRADA);
        var revision = sembrarRevision(sembrarVersion(artefacto, 2), "PENDIENTE");
        entityManager.flush();
        entityManager.clear();

        // Act
        var existen = adapter.existenNoCerradasDeVersionAnterior(revision, CERRADA);

        // Assert
        assertThat(existen).isFalse();
    }

    @Test
    void debeConsiderarTodasLasRevisionesDeLaVersionAnterior_cuandoHayVariasRevisiones() {
        // Arrange
        var versionAnterior = sembrarVersion(artefacto, 1);
        var revisionCerrada = sembrarRevision(versionAnterior, "CERRADA");
        var revisionPrevia = sembrarRevision(versionAnterior, "RESUELTA");
        sembrarObservacion(revisionCerrada, CERRADA);
        sembrarObservacion(revisionPrevia, "REABIERTA");
        var revision = sembrarRevision(sembrarVersion(artefacto, 2), "PENDIENTE");
        entityManager.flush();
        entityManager.clear();

        // Act
        var existen = adapter.existenNoCerradasDeVersionAnterior(revision, CERRADA);

        // Assert
        assertThat(existen).isTrue();
    }

    @Test
    void debeIgnorarVersionesNoInmediatasYOtrosArtefactos_cuandoSeConsultaLaVersionAnterior() {
        // Arrange
        var versionLejana = sembrarVersion(artefacto, 1);
        sembrarObservacion(sembrarRevision(versionLejana, "RESUELTA"), "SOLUCIONADA");
        var versionAnterior = sembrarVersion(artefacto, 2);
        sembrarObservacion(sembrarRevision(versionAnterior, "CERRADA"), CERRADA);
        var otroArtefacto = UtilUUID.generarNuevoUUID();
        var versionAjena = sembrarVersion(otroArtefacto, 2);
        sembrarObservacion(sembrarRevision(versionAjena, "RESUELTA"), "NUEVA");
        var revision = sembrarRevision(sembrarVersion(artefacto, 3), "PENDIENTE");
        entityManager.flush();
        entityManager.clear();

        // Act
        var existen = adapter.existenNoCerradasDeVersionAnterior(revision, CERRADA);

        // Assert
        assertThat(existen).isFalse();
    }
}
