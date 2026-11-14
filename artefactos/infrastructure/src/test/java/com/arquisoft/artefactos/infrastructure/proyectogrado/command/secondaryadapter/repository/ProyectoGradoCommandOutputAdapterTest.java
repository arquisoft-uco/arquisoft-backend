package com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.entity.ObservacionJpaEntity;
import com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.entity.RevisionAsesorJpaEntity;
import com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.entity.VersionArtefactoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import com.arquisoft.shared.util.UtilFecha;
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
class ProyectoGradoCommandOutputAdapterTest {

    @Autowired
    private ProyectoGradoCommandRepository proyectoGradoCommandRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final AppLogger logger = mock(AppLogger.class);

    private ProyectoGradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProyectoGradoCommandOutputAdapter(proyectoGradoCommandRepository, logger);
    }

    @Test
    void debeGuardarYReportarExistencia_cuandoSePersisteElProyecto() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        adapter.guardar(new ProyectoGradoEntity(id, "Sistema de gestion", UtilFecha.VACIO, UtilFecha.VACIO, null));
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(ProyectoGradoJpaEntity.class, id).getNombre()).isEqualTo("Sistema de gestion");
        assertThat(adapter.existePorId(id)).isTrue();
        assertThat(adapter.existePorId(UtilUUID.generarNuevoUUID())).isFalse();
        verify(logger).debug(ProyectoGradoKey.LOG_GUARDADO, id);
    }

    @Test
    void debeObtenerPorArtefactoYRetornarVacio_cuandoElArtefactoNoExiste() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var otroProyecto = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(proyecto).nombre("Sistema de gestion").build());
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(otroProyecto).nombre("Otro").build());
        var artefacto = UtilUUID.generarNuevoUUID();
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(artefacto)
                .proyectoGrado(proyecto)
                .versionRepositorioArtefacto(UtilUUID.generarNuevoUUID())
                .asesor(UtilUUID.generarNuevoUUID())
                .build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrado = adapter.obtenerPorArtefacto(artefacto);
        var ausente = adapter.obtenerPorArtefacto(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(encontrado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(proyecto);
            assertThat(entity.nombre()).isEqualTo("Sistema de gestion");
        });
        assertThat(ausente).isEmpty();
    }

    @Test
    void debeObtenerBloqueadoPorArtefacto() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(proyecto).nombre("Sistema de gestion").build());
        var artefacto = UtilUUID.generarNuevoUUID();
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(artefacto)
                .proyectoGrado(proyecto)
                .versionRepositorioArtefacto(UtilUUID.generarNuevoUUID())
                .asesor(UtilUUID.generarNuevoUUID())
                .build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrado = adapter.obtenerBloqueadoPorArtefacto(artefacto);
        var ausente = adapter.obtenerBloqueadoPorArtefacto(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(encontrado).hasValueSatisfying(entity -> assertThat(entity.id()).isEqualTo(proyecto));
        assertThat(ausente).isEmpty();
    }

    @Test
    void debeObtenerBloqueadoPorIdYRetornarVacio_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(id).nombre("Sistema de gestion").build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrado = adapter.obtenerBloqueadoPorId(id);
        var ausente = adapter.obtenerBloqueadoPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(encontrado).hasValueSatisfying(entity -> assertThat(entity.id()).isEqualTo(id));
        assertThat(ausente).isEmpty();
    }

    @Test
    void debeObtenerPorId_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(id).nombre("Sistema de gestion").build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var encontrado = adapter.obtenerPorId(id);
        var ausente = adapter.obtenerPorId(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(encontrado).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(id);
            assertThat(entity.nombre()).isEqualTo("Sistema de gestion");
        });
        assertThat(ausente).isEmpty();
    }

    private UUID proyectoSembrado(Instant entregadoEn, Instant finalizadoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder()
                .id(id).nombre("Sistema de gestion").entregadoEn(entregadoEn).finalizadoEn(finalizadoEn).build());
        return id;
    }

    @Test
    void debeMarcarEntregado_cuandoAunNoTieneFecha() {
        // Arrange
        var id = proyectoSembrado(null, null);
        var entregadoEn = Instant.parse("2026-11-01T10:00:00Z");
        entityManager.flush();

        // Act
        adapter.marcarEntregado(id, entregadoEn);

        // Assert
        var proyecto = adapter.obtenerPorId(id).orElseThrow();
        assertThat(proyecto.entregadoEn()).isEqualTo(entregadoEn);
        assertThat(proyecto.finalizadoEn()).isEqualTo(UtilFecha.VACIO);
        verify(logger).debug(ProyectoGradoKey.LOG_ENTREGADO_MARCADO, id, entregadoEn);
    }

    @Test
    void debeActualizarEntregado_cuandoElExistenteEsAnterior() {
        // Arrange
        var original = Instant.parse("2026-11-01T10:00:00Z");
        var id = proyectoSembrado(original, null);
        entityManager.flush();

        // Act
        var posterior = Instant.parse("2026-11-05T10:00:00Z");
        adapter.marcarEntregado(id, posterior);

        // Assert
        assertThat(adapter.obtenerPorId(id).orElseThrow().entregadoEn()).isEqualTo(posterior);
    }


    @Test
    void debeNoSobrescribirEntregado_cuandoElExistenteEsPosterior() {
        // Arrange
        var original = Instant.parse("2026-11-05T10:00:00Z");
        var id = proyectoSembrado(original, null);
        entityManager.flush();

        // Act
        adapter.marcarEntregado(id, Instant.parse("2026-11-01T10:00:00Z"));

        // Assert
        assertThat(adapter.obtenerPorId(id).orElseThrow().entregadoEn()).isEqualTo(original);
    }

    @Test
    void debeLimpiarEntregado_cuandoElExistenteNoEsPosteriorAlLimite() {
        // Arrange
        var hasta = Instant.parse("2026-11-05T10:00:00Z");
        var id = proyectoSembrado(Instant.parse("2026-11-01T10:00:00Z"), Instant.parse("2026-11-03T10:00:00Z"));
        entityManager.flush();

        // Act
        adapter.limpiarEntregado(id, hasta);

        // Assert
        var proyecto = adapter.obtenerPorId(id).orElseThrow();
        assertThat(proyecto.entregadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(proyecto.finalizadoEn()).isEqualTo(Instant.parse("2026-11-03T10:00:00Z"));
        verify(logger).debug(ProyectoGradoKey.LOG_ENTREGADO_LIMPIADO, id, hasta);
    }

    @Test
    void debeNoLimpiarEntregado_cuandoElExistenteEsPosteriorAlLimite() {
        // Arrange
        var original = Instant.parse("2026-11-09T10:00:00Z");
        var id = proyectoSembrado(original, null);
        entityManager.flush();

        // Act
        adapter.limpiarEntregado(id, Instant.parse("2026-11-05T10:00:00Z"));

        // Assert
        assertThat(adapter.obtenerPorId(id).orElseThrow().entregadoEn()).isEqualTo(original);
    }
    @Test
    void debeMarcarFinalizado_cuandoAunNoTieneFecha() {
        // Arrange
        var id = proyectoSembrado(null, null);
        var finalizadoEn = Instant.parse("2026-11-02T10:00:00Z");
        entityManager.flush();

        // Act
        adapter.marcarFinalizado(id, finalizadoEn);

        // Assert
        var proyecto = adapter.obtenerPorId(id).orElseThrow();
        assertThat(proyecto.finalizadoEn()).isEqualTo(finalizadoEn);
        assertThat(proyecto.entregadoEn()).isEqualTo(UtilFecha.VACIO);
        verify(logger).debug(ProyectoGradoKey.LOG_FINALIZADO_MARCADO, id, finalizadoEn);
    }

    @Test
    void debeNoSobrescribirFinalizado_cuandoYaTieneFecha() {
        // Arrange
        var original = Instant.parse("2026-11-02T10:00:00Z");
        var id = proyectoSembrado(null, original);
        entityManager.flush();

        // Act
        adapter.marcarFinalizado(id, Instant.parse("2026-11-06T10:00:00Z"));

        // Assert
        assertThat(adapter.obtenerPorId(id).orElseThrow().finalizadoEn()).isEqualTo(original);
    }

    @Test
    void debeMarcarCanceladoUnaVez_cuandoSeRepite() {
        // Arrange
        var id = proyectoSembrado(null, null);
        var original = Instant.parse("2026-11-03T10:00:00Z");
        entityManager.flush();

        // Act
        adapter.marcarCancelado(id, original);
        entityManager.clear();
        adapter.marcarCancelado(id, Instant.parse("2026-11-09T10:00:00Z"));
        entityManager.clear();

        // Assert
        var proyecto = adapter.obtenerPorId(id).orElseThrow();
        assertThat(proyecto.canceladoEn()).isEqualTo(original);
        assertThat(proyecto.finalizadoEn()).isEqualTo(UtilFecha.VACIO);
        verify(logger).debug(ProyectoGradoKey.LOG_CANCELADO_MARCADO, id, original);
    }

    @Test
    void debeObtenerBloqueadoPorRevisionObservacionYVersion_cuandoExistenEnElProyecto() {
        // Arrange
        var entregadoEn = Instant.parse("2026-11-01T10:00:00Z");
        var proyecto = proyectoSembrado(entregadoEn, null);
        proyectoSembrado(null, null);
        var artefacto = UtilUUID.generarNuevoUUID();
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(artefacto)
                .proyectoGrado(proyecto)
                .versionRepositorioArtefacto(UtilUUID.generarNuevoUUID())
                .asesor(UtilUUID.generarNuevoUUID())
                .build());
        var version = UtilUUID.generarNuevoUUID();
        entityManager.persist(VersionArtefactoJpaEntity.builder()
                .id(version).artefacto(artefacto).numeroVersion(1).fechaCreacion(entregadoEn)
                .uriArchivo("versiones/" + version).tamanoBytes(10L).build());
        var revision = UtilUUID.generarNuevoUUID();
        entityManager.persist(RevisionAsesorJpaEntity.builder()
                .id(revision).versionArtefacto(version).estado("PENDIENTE").fechaCreacion(entregadoEn).build());
        var observacion = UtilUUID.generarNuevoUUID();
        entityManager.persist(ObservacionJpaEntity.builder()
                .id(observacion).revisionAsesor(revision).descripcion("Detalle").estado("NUEVA").build());
        entityManager.flush();
        entityManager.clear();

        // Act
        var porRevision = adapter.obtenerBloqueadoPorRevisionAsesor(revision);
        var porObservacion = adapter.obtenerBloqueadoPorObservacion(observacion);
        var porVersion = adapter.obtenerBloqueadoPorVersionArtefacto(version);

        // Assert
        assertThat(porRevision).hasValueSatisfying(entity -> {
            assertThat(entity.id()).isEqualTo(proyecto);
            assertThat(entity.entregadoEn()).isEqualTo(entregadoEn);
        });
        assertThat(porObservacion).hasValueSatisfying(entity -> assertThat(entity.id()).isEqualTo(proyecto));
        assertThat(porVersion).hasValueSatisfying(entity -> assertThat(entity.id()).isEqualTo(proyecto));
    }

    @Test
    void debeRetornarVacio_cuandoLosIdentificadoresNoExisten() {
        // Act & Assert
        assertThat(adapter.obtenerBloqueadoPorRevisionAsesor(UtilUUID.generarNuevoUUID())).isEmpty();
        assertThat(adapter.obtenerBloqueadoPorObservacion(UtilUUID.generarNuevoUUID())).isEmpty();
        assertThat(adapter.obtenerBloqueadoPorVersionArtefacto(UtilUUID.generarNuevoUUID())).isEmpty();
    }
}
