package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.command.secondaryadapter.entity.EstadoEntregableProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(EstadoEntregableProyectoGradoQueryOutputAdapter.class)
class EstadoEntregableProyectoGradoQueryOutputAdapterTest {

    private static final Instant FECHA_BASE = Instant.parse("2026-11-10T10:00:00Z");

    @Autowired
    private EstadoEntregableProyectoGradoQueryOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        var em = entityManager.getEntityManager();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS estado_entregable (id VARCHAR(60) PRIMARY KEY, "
                + "nombre VARCHAR(60), descripcion VARCHAR(300))").executeUpdate();
        em.createNativeQuery("DELETE FROM estado_entregable").executeUpdate();
        sembrarCatalogo("DISPONIBLE_EVALUACION", "Disponible Evaluacion");
        sembrarCatalogo("EN_EVALUACION", "En Evaluacion");
        sembrarCatalogo("CERRADO", "Cerrado");
    }

    private void sembrarCatalogo(String id, String nombre) {
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO estado_entregable (id, nombre, descripcion) VALUES (?1, ?2, 'd')")
                .setParameter(1, id).setParameter(2, nombre)
                .executeUpdate();
    }

    private UUID sembrar(UUID entregable, String estado, Instant fecha) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new EstadoEntregableProyectoGradoJpaEntity(id, entregable, estado, fecha));
        return id;
    }

    @Test
    void debeRetornarHistorialAscendenteConNombre_cuandoHayVariasFilas() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var ultima = sembrar(entregable, "CERRADO", FECHA_BASE.plusSeconds(90));
        var primera = sembrar(entregable, "DISPONIBLE_EVALUACION", FECHA_BASE);
        var segunda = sembrar(entregable, "EN_EVALUACION", FECHA_BASE.plusSeconds(30));
        sembrar(UtilUUID.generarNuevoUUID(), "CERRADO", FECHA_BASE.plusSeconds(10));
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorEntregable(entregable);

        // Assert
        assertThat(resultado).extracting("id").containsExactly(primera, segunda, ultima);
        assertThat(resultado).extracting("estadoEntregable")
                .containsExactly("DISPONIBLE_EVALUACION", "EN_EVALUACION", "CERRADO");
        assertThat(resultado).extracting("estadoEntregableNombre")
                .containsExactly("Disponible Evaluacion", "En Evaluacion", "Cerrado");
        assertThat(resultado.get(0).fechaActualizacion()).isEqualTo(FECHA_BASE);
    }

    @Test
    void debeRetornarVacio_cuandoElEntregableNoTieneHistorial() {
        // Arrange
        sembrar(UtilUUID.generarNuevoUUID(), "CERRADO", FECHA_BASE);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorEntregable(UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
