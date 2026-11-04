package com.arquisoft.entregables.infrastructure.estadoentregable.query.secondaryadapter.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(EstadoEntregableQueryOutputAdapter.class)
class EstadoEntregableQueryOutputAdapterTest {

    @Autowired
    private EstadoEntregableQueryOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void prepararTabla() {
        var em = entityManager.getEntityManager();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS estado_entregable (id VARCHAR(60) PRIMARY KEY, "
                + "nombre VARCHAR(60), descripcion VARCHAR(300))").executeUpdate();
        em.createNativeQuery("DELETE FROM estado_entregable").executeUpdate();
    }

    private void sembrar(String id, String nombre, String descripcion) {
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO estado_entregable (id, nombre, descripcion) VALUES (?1, ?2, ?3)")
                .setParameter(1, id).setParameter(2, nombre).setParameter(3, descripcion)
                .executeUpdate();
    }

    @Test
    void debeRetornarLosEstadosConTodasLasColumnas_cuandoExistenEnBD() {
        // Arrange
        sembrar("DISPONIBLE_EVALUACION", "Disponible Evaluacion", "Listo");
        sembrar("EN_EVALUACION", "En Evaluacion", "Evaluando");
        sembrar("CERRADO", "Cerrado", "Cerrado ok");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.findAll();

        // Assert
        assertThat(resultado).hasSize(3);
        assertThat(resultado).anySatisfy(estado -> {
            assertThat(estado.id()).isEqualTo("EN_EVALUACION");
            assertThat(estado.nombre()).isEqualTo("En Evaluacion");
            assertThat(estado.descripcion()).isEqualTo("Evaluando");
        });
        assertThat(resultado).extracting("id").containsExactlyInAnyOrder("DISPONIBLE_EVALUACION", "EN_EVALUACION", "CERRADO");
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayEstados() {
        // Arrange
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.findAll();

        // Assert
        assertThat(resultado).isEmpty();
    }
}
