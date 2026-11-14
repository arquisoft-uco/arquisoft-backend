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
        adapter.guardar(new ProyectoGradoEntity(
                id, "Sistema de gestion", UtilFecha.VACIO, "EN_PROCESO", UtilFecha.VACIO));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = entityManager.find(ProyectoGradoJpaEntity.class, id);
        assertThat(guardado.getNombre()).isEqualTo("Sistema de gestion");
        assertThat(guardado.getEstadoProyectoId()).isEqualTo("EN_PROCESO");
        assertThat(guardado.getOcurridoEn()).isNull();
        assertThat(adapter.existePorId(id)).isTrue();
        assertThat(adapter.existePorId(UtilUUID.generarNuevoUUID())).isFalse();
        verify(logger).debug(ProyectoGradoKey.LOG_GUARDADO, id);
    }

    @Test
    void debeObtenerPorArtefactoYRetornarVacio_cuandoElArtefactoNoExiste() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var otroProyecto = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(proyecto).estadoProyectoId("EN_PROCESO").nombre("Sistema de gestion").build());
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(otroProyecto).estadoProyectoId("EN_PROCESO").nombre("Otro").build());
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
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(proyecto).estadoProyectoId("EN_PROCESO").nombre("Sistema de gestion").build());
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
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(id).estadoProyectoId("EN_PROCESO").nombre("Sistema de gestion").build());
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
        entityManager.persist(ProyectoGradoJpaEntity.builder().id(id).estadoProyectoId("EN_PROCESO").nombre("Sistema de gestion").build());
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

    private UUID proyectoSembrado(Instant entregadoEn, String estado, Instant ocurridoEn) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder()
                .id(id).nombre("Sistema de gestion").entregadoEn(entregadoEn)
                .estadoProyectoId(estado).ocurridoEn(ocurridoEn).build());
        return id;
    }

    @Test
    void debeMarcarEntregado_cuandoAunNoTieneFecha() {
        // Arrange
        var id = proyectoSembrado(null, "EN_PROCESO", null);
        var entregadoEn = Instant.parse("2026-11-01T10:00:00Z");
        entityManager.flush();

        // Act
        adapter.marcarEntregado(id, entregadoEn);

        // Assert
        var proyecto = adapter.obtenerPorId(id).orElseThrow();
        assertThat(proyecto.entregadoEn()).isEqualTo(entregadoEn);
        assertThat(proyecto.estadoProyecto()).isEqualTo("EN_PROCESO");
        verify(logger).debug(ProyectoGradoKey.LOG_ENTREGADO_MARCADO, id, entregadoEn);
    }

    @Test
    void debeActualizarEntregado_cuandoElExistenteEsAnterior() {
        // Arrange
        var original = Instant.parse("2026-11-01T10:00:00Z");
        var id = proyectoSembrado(original, "EN_PROCESO", null);
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
        var id = proyectoSembrado(original, "EN_PROCESO", null);
        entityManager.flush();

        // Act
        adapter.marcarEntregado(id, Instant.parse("2026-11-01T10:00:00Z"));

        // Assert
        assertThat(adapter.obtenerPorId(id).orElseThrow().entregadoEn()).isEqualTo(original);
    }

    @Test
    void debeLimpiarEntregadoSinTocarElEstado_cuandoElExistenteNoEsPosteriorAlLimite() {
        // Arrange
        var hasta = Instant.parse("2026-11-05T10:00:00Z");
        var ocurridoEn = Instant.parse("2026-11-03T10:00:00Z");
        var id = proyectoSembrado(Instant.parse("2026-11-01T10:00:00Z"), "FINALIZADO", ocurridoEn);
        entityManager.flush();

        // Act
        adapter.limpiarEntregado(id, hasta);

        // Assert
        var proyecto = adapter.obtenerPorId(id).orElseThrow();
        assertThat(proyecto.entregadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(proyecto.estadoProyecto()).isEqualTo("FINALIZADO");
        assertThat(proyecto.ocurridoEn()).isEqualTo(ocurridoEn);
        verify(logger).debug(ProyectoGradoKey.LOG_ENTREGADO_LIMPIADO, id, hasta);
    }

    @Test
    void debeNoLimpiarEntregado_cuandoElExistenteEsPosteriorAlLimite() {
        // Arrange
        var original = Instant.parse("2026-11-09T10:00:00Z");
        var id = proyectoSembrado(original, "EN_PROCESO", null);
        entityManager.flush();

        // Act
        adapter.limpiarEntregado(id, Instant.parse("2026-11-05T10:00:00Z"));

        // Assert
        assertThat(adapter.obtenerPorId(id).orElseThrow().entregadoEn()).isEqualTo(original);
    }

    @Test
    void debeActualizarEstado_cuandoElEventoEsMasNuevo() {
        // Arrange
        var guardado = Instant.parse("2026-11-02T10:00:00Z");
        var id = proyectoSembrado(null, "ATRASADO", guardado);
        var nuevo = Instant.parse("2026-11-06T10:00:00Z");
        entityManager.flush();

        // Act
        adapter.actualizarEstado(id, "FINALIZADO", nuevo);

        // Assert
        var proyecto = adapter.obtenerPorId(id).orElseThrow();
        assertThat(proyecto.estadoProyecto()).isEqualTo("FINALIZADO");
        assertThat(proyecto.ocurridoEn()).isEqualTo(nuevo);
        assertThat(proyecto.entregadoEn()).isEqualTo(UtilFecha.VACIO);
        verify(logger).debug(ProyectoGradoKey.LOG_ESTADO_ACTUALIZADO, id, "FINALIZADO", nuevo);
    }

    @Test
    void debeActualizarEstado_cuandoNoHayOcurridoEnGuardado() {
        // Arrange
        var id = proyectoSembrado(null, "EN_PROCESO", null);
        var ocurridoEn = Instant.parse("2026-11-06T10:00:00Z");
        entityManager.flush();

        // Act
        adapter.actualizarEstado(id, "CANCELADO", ocurridoEn);

        // Assert
        var proyecto = adapter.obtenerPorId(id).orElseThrow();
        assertThat(proyecto.estadoProyecto()).isEqualTo("CANCELADO");
        assertThat(proyecto.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeNoActualizarEstado_cuandoElEventoEsIgualAlGuardado() {
        // Arrange
        var guardado = Instant.parse("2026-11-02T10:00:00Z");
        var id = proyectoSembrado(null, "FINALIZADO", guardado);
        entityManager.flush();

        // Act
        adapter.actualizarEstado(id, "CANCELADO", guardado);

        // Assert
        var proyecto = adapter.obtenerPorId(id).orElseThrow();
        assertThat(proyecto.estadoProyecto()).isEqualTo("FINALIZADO");
        assertThat(proyecto.ocurridoEn()).isEqualTo(guardado);
    }

    @Test
    void debeNoActualizarEstado_cuandoElEventoEsMasViejoQueElGuardado() {
        // Arrange
        var guardado = Instant.parse("2026-11-02T10:00:00Z");
        var id = proyectoSembrado(null, "FINALIZADO", guardado);
        entityManager.flush();

        // Act
        adapter.actualizarEstado(id, "ATRASADO", guardado.minusSeconds(3600));

        // Assert
        var proyecto = adapter.obtenerPorId(id).orElseThrow();
        assertThat(proyecto.estadoProyecto()).isEqualTo("FINALIZADO");
        assertThat(proyecto.ocurridoEn()).isEqualTo(guardado);
    }

    @Test
    void debeObtenerBloqueadoPorRevisionObservacionYVersion_cuandoExistenEnElProyecto() {
        // Arrange
        var entregadoEn = Instant.parse("2026-11-01T10:00:00Z");
        var proyecto = proyectoSembrado(entregadoEn, "EN_PROCESO", null);
        proyectoSembrado(null, "EN_PROCESO", null);
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
