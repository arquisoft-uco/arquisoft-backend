package com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.entity.RevisionAsesorEntity;
import com.arquisoft.artefactos.infrastructure.observacion.support.ObservacionTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.entity.RevisionAsesorJpaEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.ArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.EstudianteArtefactoTestEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.support.VersionArtefactoTestEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.CambiarEstadoRevisionAsesorKey;
import com.arquisoft.shared.message.key.artefactos.RevisionAsesorKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class RevisionAsesorCommandOutputAdapterTest {

    private static final Instant FECHA = Instant.parse("2026-10-28T10:00:00Z");

    @Autowired
    private RevisionAsesorCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private RevisionAsesorCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RevisionAsesorCommandOutputAdapter(repository, logger);
    }

    private UUID sembrarVersion(UUID artefacto, int numero) {
        var version = UtilUUID.generarNuevoUUID();
        entityManager.persist(new VersionArtefactoTestEntity(version, artefacto, numero));
        return version;
    }

    private UUID sembrarRevisionDeVersion(UUID version, String estado, Instant fecha) {
        var revision = UtilUUID.generarNuevoUUID();
        entityManager.persist(RevisionAsesorJpaEntity.builder()
                .id(revision).versionArtefacto(version).estado(estado).fechaCreacion(fecha).build());
        return revision;
    }

    private UUID sembrarRevision(UUID artefacto, String estado) {
        return sembrarRevisionDeVersion(sembrarVersion(artefacto, 1), estado, FECHA);
    }

    @Test
    void debeObtenerPorId_cuandoExiste() {
        // Arrange
        var revision = sembrarRevision(UtilUUID.generarNuevoUUID(), "PENDIENTE");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerPorId(revision);

        // Assert
        assertThat(resultado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(revision);
            assertThat(entity.estado()).isEqualTo("PENDIENTE");
            assertThat(entity.versionArtefacto()).isNotNull();
            assertThat(entity.fechaCreacion()).isEqualTo(FECHA);
        });
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Act
        var resultado = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRegistrarConFecha_cuandoSeGuarda() {
        // Arrange
        var version = sembrarVersion(UtilUUID.generarNuevoUUID(), 1);
        var id = UtilUUID.generarNuevoUUID();
        entityManager.flush();

        // Act
        adapter.registrar(new RevisionAsesorEntity(id, version, "PENDIENTE", FECHA));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardada = entityManager.find(RevisionAsesorJpaEntity.class, id);
        assertThat(guardada.getVersionArtefacto()).isEqualTo(version);
        assertThat(guardada.getEstado()).isEqualTo("PENDIENTE");
        assertThat(guardada.getFechaCreacion()).isEqualTo(FECHA);
        verify(logger).debug(RevisionAsesorKey.LOG_GUARDADA, id);
    }

    @Test
    void debeActualizarSoloElEstadoDeLaRevision_cuandoSeActualiza() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var objetivo = sembrarRevision(artefacto, "PENDIENTE");
        var otra = sembrarRevision(artefacto, "PENDIENTE");
        entityManager.flush();

        // Act
        adapter.actualizarEstado(objetivo, "RESUELTA");
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(RevisionAsesorJpaEntity.class, objetivo).getEstado()).isEqualTo("RESUELTA");
        assertThat(entityManager.find(RevisionAsesorJpaEntity.class, otra).getEstado()).isEqualTo("PENDIENTE");
        verify(logger).debug(CambiarEstadoRevisionAsesorKey.LOG_ESTADO_ACTUALIZADO, objetivo);
    }

    @Test
    void existeParticipacion_debeSerVerdaderoCuandoElEstudianteEstaEnElArtefacto() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoTestEntity(artefacto));
        entityManager.persist(new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), artefacto, estudiante));
        var revision = sembrarRevision(artefacto, "PENDIENTE");
        entityManager.flush();

        // Act
        var participa = adapter.existeParticipacion(revision, estudiante);

        // Assert
        assertThat(participa).isTrue();
    }

    @Test
    void existeParticipacion_debeSerFalsoCuandoElEstudianteEsDeOtroArtefactoOInexistente() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var otroArtefacto = UtilUUID.generarNuevoUUID();
        var estudianteAjeno = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ArtefactoTestEntity(artefacto));
        entityManager.persist(new ArtefactoTestEntity(otroArtefacto));
        entityManager.persist(
                new EstudianteArtefactoTestEntity(UtilUUID.generarNuevoUUID(), otroArtefacto, estudianteAjeno));
        var revision = sembrarRevision(artefacto, "PENDIENTE");
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.existeParticipacion(revision, estudianteAjeno)).isFalse();
        assertThat(adapter.existeParticipacion(UtilUUID.generarNuevoUUID(), estudianteAjeno)).isFalse();
    }

    @Test
    void debeExistirAbierta_cuandoHayRevisionNoCerradaEnLaVersion() {
        // Arrange
        var version = sembrarVersion(UtilUUID.generarNuevoUUID(), 1);
        sembrarRevisionDeVersion(version, "CERRADA", FECHA);
        sembrarRevisionDeVersion(version, "PENDIENTE", FECHA.plusSeconds(60));
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.existeAbiertaPorVersionArtefacto(version)).isTrue();
    }

    @Test
    void noDebeExistirAbierta_cuandoSoloHayCerradasOSinRevisiones() {
        // Arrange
        var conCerrada = sembrarVersion(UtilUUID.generarNuevoUUID(), 1);
        var sinRevision = sembrarVersion(UtilUUID.generarNuevoUUID(), 1);
        sembrarRevisionDeVersion(conCerrada, "CERRADA", FECHA);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.existeAbiertaPorVersionArtefacto(conCerrada)).isFalse();
        assertThat(adapter.existeAbiertaPorVersionArtefacto(sinRevision)).isFalse();
    }

    @Test
    void debeDevolverMasRecienteDeUltimaVersion_cuandoHayReapertura() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var primera = sembrarVersion(artefacto, 1);
        var ultima = sembrarVersion(artefacto, 2);
        sembrarRevisionDeVersion(primera, "RESUELTA", FECHA.plusSeconds(600));
        sembrarRevisionDeVersion(ultima, "CERRADA", FECHA);
        var reapertura = sembrarRevisionDeVersion(ultima, "PENDIENTE", FECHA.plusSeconds(60));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerMasRecienteDeUltimaVersion(artefacto);

        // Assert
        assertThat(resultado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(reapertura);
            assertThat(entity.versionArtefacto()).isEqualTo(ultima);
            assertThat(entity.estado()).isEqualTo("PENDIENTE");
        });
    }

    @Test
    void debeDevolverVacio_cuandoUltimaVersionSinRevision() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var primera = sembrarVersion(artefacto, 1);
        sembrarVersion(artefacto, 2);
        sembrarRevisionDeVersion(primera, "EN_PROGRESO", FECHA);
        entityManager.flush();
        entityManager.clear();

        // Act & Assert
        assertThat(adapter.obtenerMasRecienteDeUltimaVersion(artefacto)).isEmpty();
    }

    @Test
    void debeDevolverVacio_cuandoSinVersiones() {
        // Act & Assert
        assertThat(adapter.obtenerMasRecienteDeUltimaVersion(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    @Test
    void debeExistirVersionPosterior_cuandoElArtefactoTieneUnaVersionMayor() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var primera = sembrarVersion(artefacto, 1);
        sembrarVersion(artefacto, 2);
        var revision = sembrarRevisionDeVersion(primera, "EN_PROGRESO", FECHA);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.existeVersionPosterior(revision)).isTrue();
    }

    @Test
    void noDebeExistirVersionPosterior_cuandoEsLaUltimaOEsDeOtroArtefacto() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var ultima = sembrarVersion(artefacto, 2);
        sembrarVersion(UtilUUID.generarNuevoUUID(), 5);
        var revision = sembrarRevisionDeVersion(ultima, "EN_PROGRESO", FECHA);
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.existeVersionPosterior(revision)).isFalse();
    }

    @Test
    void debeObtenerLaRevisionDeLaObservacion_cuandoLaObservacionExiste() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var objetivo = sembrarRevision(artefacto, "PENDIENTE");
        var otra = sembrarRevision(artefacto, "RESUELTA");
        var observacion = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ObservacionTestEntity(observacion, objetivo, "Detalle", "NUEVA"));
        entityManager.persist(new ObservacionTestEntity(UtilUUID.generarNuevoUUID(), otra, "Otro", "NUEVA"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerBloqueadaPorObservacion(observacion);

        // Assert
        assertThat(resultado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(objetivo);
            assertThat(entity.estado()).isEqualTo("PENDIENTE");
        });
    }

    @Test
    void debeObtenerLaRevisionMasRecienteDeLaVersionSiguiente() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var versionUno = sembrarVersion(artefacto, 1);
        var versionDos = sembrarVersion(artefacto, 2);
        var versionTres = sembrarVersion(artefacto, 3);
        var revisionOriginal = sembrarRevisionDeVersion(versionUno, "RESUELTA", FECHA);
        sembrarRevisionDeVersion(versionDos, "CERRADA", FECHA);
        var masReciente = sembrarRevisionDeVersion(versionDos, "PENDIENTE", FECHA.plusSeconds(60));
        sembrarRevisionDeVersion(versionTres, "PENDIENTE", FECHA.plusSeconds(120));
        var observacion = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ObservacionTestEntity(observacion, revisionOriginal, "Detalle", "SOLUCIONADA"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerBloqueadaDeVersionSiguientePorObservacion(observacion);

        // Assert
        assertThat(resultado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(masReciente);
            assertThat(entity.versionArtefacto()).isEqualTo(versionDos);
        });
    }

    @Test
    void debeDevolverVacio_cuandoNoHayVersionSiguienteONoTieneRevision() {
        // Arrange
        var artefactoSinSiguiente = UtilUUID.generarNuevoUUID();
        var revisionUnica = sembrarRevision(artefactoSinSiguiente, "RESUELTA");
        var observacionSinSiguiente = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ObservacionTestEntity(observacionSinSiguiente, revisionUnica, "Uno", "SOLUCIONADA"));
        var artefactoSinRevision = UtilUUID.generarNuevoUUID();
        var versionUno = sembrarVersion(artefactoSinRevision, 1);
        sembrarVersion(artefactoSinRevision, 2);
        var revisionUno = sembrarRevisionDeVersion(versionUno, "RESUELTA", FECHA);
        var observacionSinRevision = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ObservacionTestEntity(observacionSinRevision, revisionUno, "Dos", "SOLUCIONADA"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var sinSiguiente = adapter.obtenerBloqueadaDeVersionSiguientePorObservacion(observacionSinSiguiente);
        var sinRevision = adapter.obtenerBloqueadaDeVersionSiguientePorObservacion(observacionSinRevision);
        var inexistente = adapter.obtenerBloqueadaDeVersionSiguientePorObservacion(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(sinSiguiente).isEmpty();
        assertThat(sinRevision).isEmpty();
        assertThat(inexistente).isEmpty();
    }

    @Test
    void debeIgnorarLaVersionDosPosterior_cuandoSoloEstaEsaTieneRevision() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var versionUno = sembrarVersion(artefacto, 1);
        sembrarVersion(artefacto, 2);
        var versionTres = sembrarVersion(artefacto, 3);
        var revisionUno = sembrarRevisionDeVersion(versionUno, "RESUELTA", FECHA);
        sembrarRevisionDeVersion(versionTres, "PENDIENTE", FECHA);
        var observacion = UtilUUID.generarNuevoUUID();
        entityManager.persist(new ObservacionTestEntity(observacion, revisionUno, "Detalle", "SOLUCIONADA"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerBloqueadaDeVersionSiguientePorObservacion(observacion);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeDevolverVacio_cuandoLaObservacionNoExiste() {
        // Act & Assert
        assertThat(adapter.obtenerBloqueadaPorObservacion(UtilUUID.generarNuevoUUID())).isEmpty();
    }
}
