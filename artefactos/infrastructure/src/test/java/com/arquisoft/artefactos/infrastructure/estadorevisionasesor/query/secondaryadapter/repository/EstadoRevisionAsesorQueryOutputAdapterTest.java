package com.arquisoft.artefactos.infrastructure.estadorevisionasesor.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estadorevisionasesor.query.readmodel.EstadoRevisionAsesorReadModel;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EstadoRevisionAsesorQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EstadoRevisionAsesorQueryRepository repository;

    private EstadoRevisionAsesorQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoRevisionAsesorQueryOutputAdapter(repository);
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS estado_revision_asesor (
                    id VARCHAR(60) NOT NULL,
                    nombre VARCHAR(60) NOT NULL,
                    descripcion VARCHAR(300) NOT NULL,
                    orden INTEGER NOT NULL,
                    PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.flush();
    }

    private void sembrarEstado(String id, String nombre, String descripcion, int orden) {
        entityManager.createNativeQuery(
                        "INSERT INTO estado_revision_asesor (id, nombre, descripcion, orden) VALUES (?, ?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, nombre)
                .setParameter(3, descripcion)
                .setParameter(4, orden)
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void debeRetornarLosEstadosOrdenadosPorOrden_cuandoSeInsertanDesordenados() {
        // Arrange
        sembrarEstado("RESUELTA", "Resuelta", "Ajustes finalizados", 3);
        sembrarEstado("CERRADA", "Cerrada", "Asesor cerró", 4);
        sembrarEstado("PENDIENTE", "Pendiente", "Nueva", 1);
        sembrarEstado("EN_PROGRESO", "En progreso", "Ajustes iniciados", 2);

        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado)
                .extracting(EstadoRevisionAsesorReadModel::id)
                .containsExactly("PENDIENTE", "EN_PROGRESO", "RESUELTA", "CERRADA");
    }

    @Test
    void debeProyectarTodasLasColumnas_cuandoLeeUnEstadoConDescripcionEnElLimite() {
        // Arrange
        var descripcionLarga = "d".repeat(300);
        sembrarEstado("PENDIENTE", "Pendiente", descripcionLarga, 1);

        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).singleElement().satisfies(estado -> {
            assertThat(estado.id()).isEqualTo("PENDIENTE");
            assertThat(estado.nombre()).isEqualTo("Pendiente");
            assertThat(estado.descripcion()).isEqualTo(descripcionLarga);
        });
    }

    @Test
    void debeRetornarVacio_cuandoNoHayFilas() {
        // Act
        var resultado = adapter.consultarTodos();

        // Assert
        assertThat(resultado).isEmpty();
    }
}
