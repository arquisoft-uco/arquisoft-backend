package com.arquisoft.entregables.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import com.arquisoft.entregables.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ProyectoGradoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
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
        adapter.guardar(new ProyectoGradoEntity(id, "Sistema de gestion", "EN_PROCESO"));
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(ProyectoGradoJpaEntity.class, id).getNombre()).isEqualTo("Sistema de gestion");
        assertThat(adapter.existePorId(id)).isTrue();
        assertThat(adapter.existePorId(UtilUUID.generarNuevoUUID())).isFalse();
        verify(logger).debug(ProyectoGradoKey.LOG_GUARDADO, id);
    }

    @Test
    void debeCambiarSoloElEstado_cuandoSeActualizaElProyecto() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        adapter.guardar(new ProyectoGradoEntity(id, "Sistema de gestion", "EN_PROCESO"));
        adapter.guardar(new ProyectoGradoEntity(otro, "Otro proyecto", "EN_PROCESO"));
        entityManager.flush();

        // Act
        adapter.actualizarEstado(id, "FINALIZADO");
        entityManager.clear();

        // Assert
        var actualizado = entityManager.find(ProyectoGradoJpaEntity.class, id);
        assertThat(actualizado.getEstadoProyectoId()).isEqualTo("FINALIZADO");
        assertThat(actualizado.getNombre()).isEqualTo("Sistema de gestion");
        assertThat(entityManager.find(ProyectoGradoJpaEntity.class, otro).getEstadoProyectoId())
                .isEqualTo("EN_PROCESO");
        verify(logger).debug(ProyectoGradoKey.LOG_ESTADO_ACTUALIZADO, id, "FINALIZADO");
    }

    @Test
    void debeLeerEstadoCancelado_cuandoSeActualiza() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        adapter.guardar(new ProyectoGradoEntity(id, "Sistema de gestion", "EN_PROCESO"));
        entityManager.flush();

        // Act
        adapter.actualizarEstado(id, "CANCELADO");
        entityManager.clear();

        // Assert
        assertThat(adapter.obtenerPorId(id)).hasValue(new ProyectoGradoEntity(id, "Sistema de gestion", "CANCELADO"));
        verify(logger).debug(ProyectoGradoKey.LOG_ESTADO_ACTUALIZADO, id, "CANCELADO");
    }

    @Test
    void debeObtenerElProyectoConSuEstado_cuandoSeConsultaPorId() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        adapter.guardar(new ProyectoGradoEntity(id, "Sistema de gestion", "FINALIZADO"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).hasValue(new ProyectoGradoEntity(id, "Sistema de gestion", "FINALIZADO"));
        assertThat(adapter.obtenerPorId(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    @Test
    void debeObtenerProyectoBloqueado_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        adapter.guardar(new ProyectoGradoEntity(id, "Sistema de gestion", "EN_PROCESO"));
        adapter.guardar(new ProyectoGradoEntity(UtilUUID.generarNuevoUUID(), "Otro proyecto", "EN_PROCESO"));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerBloqueadoPorId(id);

        // Assert
        assertThat(resultado).hasValue(new ProyectoGradoEntity(id, "Sistema de gestion", "EN_PROCESO"));
        assertThat(adapter.obtenerBloqueadoPorId(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    @Test
    void debeObtenerBloqueadoElProyectoDelEntregable_cuandoElEntregableExiste() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var otroProyecto = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        adapter.guardar(new ProyectoGradoEntity(proyectoGrado, "Sistema de gestion", "EN_PROCESO"));
        adapter.guardar(new ProyectoGradoEntity(otroProyecto, "Otro proyecto", "EN_PROCESO"));
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
