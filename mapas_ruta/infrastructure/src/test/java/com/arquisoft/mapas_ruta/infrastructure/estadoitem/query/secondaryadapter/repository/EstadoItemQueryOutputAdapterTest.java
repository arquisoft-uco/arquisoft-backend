package com.arquisoft.mapas_ruta.infrastructure.estadoitem.query.secondaryadapter.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(EstadoItemQueryOutputAdapter.class)
class EstadoItemQueryOutputAdapterTest {

    @Autowired
    private EstadoItemQueryOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void prepararTabla() {
        var em = entityManager.getEntityManager();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS estado_item (id VARCHAR(60) PRIMARY KEY, "
                + "nombre VARCHAR(60), descripcion VARCHAR(300))").executeUpdate();
        em.createNativeQuery("DELETE FROM estado_item").executeUpdate();
    }

    private void sembrar(String id, String nombre, String descripcion) {
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO estado_item (id, nombre, descripcion) VALUES (?1, ?2, ?3)")
                .setParameter(1, id).setParameter(2, nombre).setParameter(3, descripcion)
                .executeUpdate();
    }

    @Test
    void debeRetornarLosEstadosConTodasLasColumnas_cuandoExistenEnBD() {
        // Arrange
        sembrar("NUEVA", "Nueva", "Sin iniciar");
        sembrar("EN_PROGRESO", "En progreso", "En curso");
        sembrar("FINALIZADA", "Finalizada", "Terminada");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.findAll();

        // Assert
        assertThat(resultado).hasSize(3);
        assertThat(resultado).anySatisfy(estado -> {
            assertThat(estado.id()).isEqualTo("EN_PROGRESO");
            assertThat(estado.nombre()).isEqualTo("En progreso");
            assertThat(estado.descripcion()).isEqualTo("En curso");
        });
        assertThat(resultado).extracting("id").containsExactlyInAnyOrder("NUEVA", "EN_PROGRESO", "FINALIZADA");
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
