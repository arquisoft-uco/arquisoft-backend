package com.arquisoft.mapas_ruta.infrastructure.planreal.command.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.entity.PlanRealEntity;
import com.arquisoft.mapas_ruta.infrastructure.planreal.command.secondaryadapter.entity.PlanRealJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@DataJpaTest
@Import(PlanRealCommandOutputAdapter.class)
class PlanRealCommandOutputAdapterTest {

    @Autowired
    private PlanRealCommandOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @MockitoBean
    private AppLogger logger;

    @Test
    void debeRegistrar_cuandoEntityValida() {
        // Arrange
        var entity = new PlanRealEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 4, "Hecho");

        // Act
        adapter.registrar(entity);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = entityManager.find(PlanRealJpaEntity.class, entity.id());
        assertThat(guardado.getTareaId()).isEqualTo(entity.tarea());
        assertThat(guardado.getTiempoRealDias()).isEqualTo(4);
        assertThat(guardado.getDescripcionReal()).isEqualTo("Hecho");
        verify(logger).debug(any(ClaveMensaje.class), eq(entity.tarea()));
    }

    @Test
    void debeIndicarExistencia_porTarea() {
        // Arrange
        var conPlan = UtilUUID.generarNuevoUUID();
        adapter.registrar(new PlanRealEntity(UtilUUID.generarNuevoUUID(), conPlan, 2, "x"));
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.existePorTarea(conPlan)).isTrue();
        assertThat(adapter.existePorTarea(UtilUUID.generarNuevoUUID())).isFalse();
    }

    @Test
    void debeActualizarSinCambiarId_cuandoYaExistePlanDeLaTarea() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        adapter.registrar(new PlanRealEntity(id, tarea, 2, "Antes"));
        adapter.registrar(new PlanRealEntity(otro, UtilUUID.generarNuevoUUID(), 9, "Otro"));
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.actualizarPorTarea(tarea, 5, "Despues");
        entityManager.flush();
        entityManager.clear();

        // Assert
        var actualizado = entityManager.find(PlanRealJpaEntity.class, id);
        assertThat(actualizado.getTiempoRealDias()).isEqualTo(5);
        assertThat(actualizado.getDescripcionReal()).isEqualTo("Despues");
        assertThat(actualizado.getTareaId()).isEqualTo(tarea);
        assertThat(entityManager.find(PlanRealJpaEntity.class, otro).getTiempoRealDias()).isEqualTo(9);
    }

    @Test
    void debeIndicarExistencia_porId() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        adapter.registrar(new PlanRealEntity(id, UtilUUID.generarNuevoUUID(), 2, "x"));
        entityManager.flush();

        // Act & Assert
        assertThat(adapter.existePorId(id)).isTrue();
        assertThat(adapter.existePorId(UtilUUID.generarNuevoUUID())).isFalse();
    }

    @Test
    void debeActualizarSoloElPlanIndicado_cuandoActualizaPorId() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var tarea = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        adapter.registrar(new PlanRealEntity(id, tarea, 2, "Antes"));
        adapter.registrar(new PlanRealEntity(otro, UtilUUID.generarNuevoUUID(), 9, "Otro"));
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.actualizarPorId(id, 7, "Despues");
        entityManager.flush();
        entityManager.clear();

        // Assert
        var actualizado = entityManager.find(PlanRealJpaEntity.class, id);
        assertThat(actualizado.getTiempoRealDias()).isEqualTo(7);
        assertThat(actualizado.getDescripcionReal()).isEqualTo("Despues");
        assertThat(actualizado.getTareaId()).isEqualTo(tarea);
        var intacto = entityManager.find(PlanRealJpaEntity.class, otro);
        assertThat(intacto.getTiempoRealDias()).isEqualTo(9);
        assertThat(intacto.getDescripcionReal()).isEqualTo("Otro");
        verify(logger).debug(any(ClaveMensaje.class), eq(id));
    }

    @Test
    void debeGuardarNull_cuandoDescripcionVaciaPorId() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        adapter.registrar(new PlanRealEntity(id, UtilUUID.generarNuevoUUID(), 2, "Antes"));
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.actualizarPorId(id, 2, UtilTexto.VACIO);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(PlanRealJpaEntity.class, id).getDescripcionReal()).isNull();
    }

    @Test
    void debeGuardarNull_cuandoDescripcionVacia() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        var idVacio = UtilUUID.generarNuevoUUID();
        adapter.registrar(new PlanRealEntity(idVacio, UtilUUID.generarNuevoUUID(), 1, UtilTexto.VACIO));
        adapter.registrar(new PlanRealEntity(id, tarea, 2, "Antes"));
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.actualizarPorTarea(tarea, 2, UtilTexto.VACIO);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(entityManager.find(PlanRealJpaEntity.class, idVacio).getDescripcionReal()).isNull();
        assertThat(entityManager.find(PlanRealJpaEntity.class, id).getDescripcionReal()).isNull();
    }
}
