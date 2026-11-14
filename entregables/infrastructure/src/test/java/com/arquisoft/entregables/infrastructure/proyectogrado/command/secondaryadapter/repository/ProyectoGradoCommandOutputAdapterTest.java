package com.arquisoft.entregables.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import com.arquisoft.entregables.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ProyectoGradoKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
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
        adapter.guardar(new ProyectoGradoEntity(id, "Sistema de gestion", "EN_PROCESO", UtilFecha.VACIO));
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(ProyectoGradoJpaEntity.class, id).getNombre()).isEqualTo("Sistema de gestion");
        assertThat(adapter.existePorId(id)).isTrue();
        assertThat(adapter.existePorId(UtilUUID.generarNuevoUUID())).isFalse();
        verify(logger).debug(ProyectoGradoKey.LOG_GUARDADO, id);
    }

    @Test
    void debeActualizarSoloElEstadoYLaFecha_cuandoElEventoEsMasNuevo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        var guardado = Instant.parse("2026-11-02T10:00:00Z");
        var nuevo = Instant.parse("2026-11-06T10:00:00Z");
        entityManager.persist(ProyectoGradoJpaEntity.builder()
                .id(id).nombre("Sistema de gestion").estadoProyectoId("ATRASADO").ocurridoEn(guardado).build());
        adapter.guardar(new ProyectoGradoEntity(otro, "Otro proyecto", "EN_PROCESO", UtilFecha.VACIO));
        entityManager.flush();

        // Act
        adapter.actualizarEstado(id, "FINALIZADO", nuevo);
        entityManager.clear();

        // Assert
        var actualizado = entityManager.find(ProyectoGradoJpaEntity.class, id);
        assertThat(actualizado.getEstadoProyectoId()).isEqualTo("FINALIZADO");
        assertThat(actualizado.getOcurridoEn()).isEqualTo(nuevo);
        assertThat(actualizado.getNombre()).isEqualTo("Sistema de gestion");
        assertThat(entityManager.find(ProyectoGradoJpaEntity.class, otro).getEstadoProyectoId())
                .isEqualTo("EN_PROCESO");
        verify(logger).debug(ProyectoGradoKey.LOG_ESTADO_ACTUALIZADO, id, "FINALIZADO", nuevo);
    }

    @Test
    void debeActualizarEstado_cuandoNoHayOcurridoEnGuardado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-06T10:00:00Z");
        adapter.guardar(new ProyectoGradoEntity(id, "Sistema de gestion", "EN_PROCESO", UtilFecha.VACIO));
        entityManager.flush();

        // Act
        adapter.actualizarEstado(id, "CANCELADO", ocurridoEn);
        entityManager.clear();

        // Assert
        assertThat(adapter.obtenerPorId(id))
                .hasValue(new ProyectoGradoEntity(id, "Sistema de gestion", "CANCELADO", ocurridoEn));
    }

    @Test
    void debeNoActualizarEstado_cuandoElEventoEsIgualAlGuardado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var guardado = Instant.parse("2026-11-02T10:00:00Z");
        entityManager.persist(ProyectoGradoJpaEntity.builder()
                .id(id).nombre("Sistema de gestion").estadoProyectoId("FINALIZADO").ocurridoEn(guardado).build());
        entityManager.flush();

        // Act
        adapter.actualizarEstado(id, "CANCELADO", guardado);
        entityManager.clear();

        // Assert
        assertThat(adapter.obtenerPorId(id))
                .hasValue(new ProyectoGradoEntity(id, "Sistema de gestion", "FINALIZADO", guardado));
    }

    @Test
    void debeNoActualizarEstado_cuandoElEventoEsMasViejoQueElGuardado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var guardado = Instant.parse("2026-11-02T10:00:00Z");
        entityManager.persist(ProyectoGradoJpaEntity.builder()
                .id(id).nombre("Sistema de gestion").estadoProyectoId("FINALIZADO").ocurridoEn(guardado).build());
        entityManager.flush();

        // Act
        adapter.actualizarEstado(id, "ATRASADO", guardado.minusSeconds(3600));
        entityManager.clear();

        // Assert
        assertThat(adapter.obtenerPorId(id))
                .hasValue(new ProyectoGradoEntity(id, "Sistema de gestion", "FINALIZADO", guardado));
    }

    @Test
    void debeObtenerElProyectoConSuEstado_cuandoSeConsultaPorId() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        adapter.guardar(new ProyectoGradoEntity(id, "Sistema de gestion", "FINALIZADO", UtilFecha.VACIO));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).hasValue(new ProyectoGradoEntity(id, "Sistema de gestion", "FINALIZADO", UtilFecha.VACIO));
        assertThat(adapter.obtenerPorId(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    @Test
    void debeObtenerProyectoBloqueado_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        adapter.guardar(new ProyectoGradoEntity(id, "Sistema de gestion", "EN_PROCESO", UtilFecha.VACIO));
        adapter.guardar(new ProyectoGradoEntity(UtilUUID.generarNuevoUUID(), "Otro proyecto", "EN_PROCESO", UtilFecha.VACIO));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerBloqueadoPorId(id);

        // Assert
        assertThat(resultado).hasValue(new ProyectoGradoEntity(id, "Sistema de gestion", "EN_PROCESO", UtilFecha.VACIO));
        assertThat(adapter.obtenerBloqueadoPorId(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    @Test
    void debeObtenerBloqueadoElProyectoDelEntregable_cuandoElEntregableExiste() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var otroProyecto = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        adapter.guardar(new ProyectoGradoEntity(proyectoGrado, "Sistema de gestion", "EN_PROCESO", UtilFecha.VACIO));
        adapter.guardar(new ProyectoGradoEntity(otroProyecto, "Otro proyecto", "EN_PROCESO", UtilFecha.VACIO));
        entityManager.persist(new EntregableProyectoGradoJpaEntity(
                entregable, proyectoGrado, 1, UtilUUID.generarNuevoUUID()));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerBloqueadoPorEntregable(entregable);

        // Assert
        assertThat(resultado).map(ProyectoGradoEntity::id).contains(proyectoGrado);
        assertThat(adapter.obtenerBloqueadoPorEntregable(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    @Test
    void debeBloquearElProyecto_cuandoSeLeePorEntregableParaCambiarSuEstado() throws Exception {
        // Arrange
        var metodo = ProyectoGradoCommandRepository.class.getMethod("buscarBloqueadoPorEntregable", UUID.class);

        // Act
        var lock = metodo.getAnnotation(Lock.class);

        // Assert
        assertThat(lock.value()).isEqualTo(LockModeType.PESSIMISTIC_WRITE);
    }
}
