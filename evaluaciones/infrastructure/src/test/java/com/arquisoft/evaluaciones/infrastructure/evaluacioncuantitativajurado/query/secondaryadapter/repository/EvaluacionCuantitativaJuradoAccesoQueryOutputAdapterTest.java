package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.secondaryadapter.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// Mismo esquema de soporte que EvaluacionCuantitativaJuradoQueryOutputAdapterTest: el @Subselect
// une evaluacion_cuantitativa_jurado con item_cuantitativo_jurado y Hibernate no genera esas tablas.
@DataJpaTest
class EvaluacionCuantitativaJuradoAccesoQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionCuantitativaJuradoAccesoQueryRepository repository;

    private EvaluacionCuantitativaJuradoAccesoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionCuantitativaJuradoAccesoQueryOutputAdapter(repository);
        crearEsquemaDeSoporte();
    }

    private void crearEsquemaDeSoporte() {
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS categoria_item_cuantitativo_jurado (
                    id UUID NOT NULL, nombre VARCHAR(100) NOT NULL,
                    descripcion VARCHAR(300) NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS item_cuantitativo_jurado (
                    id UUID NOT NULL, nombre VARCHAR(100) NOT NULL, descripcion VARCHAR(300) NOT NULL,
                    categoria_id UUID NOT NULL, valor INTEGER NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS evaluacion_cuantitativa_jurado (
                    id UUID NOT NULL, evaluacion_jurado_id UUID NOT NULL,
                    item_id UUID NOT NULL, puntaje INTEGER NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.flush();
    }

    private UUID sembrarEvaluacionCuantitativa() {
        var categoria = UUID.randomUUID();
        var item = UUID.randomUUID();
        var evaluacionCuantitativa = UUID.randomUUID();

        entityManager.createNativeQuery(
                "INSERT INTO categoria_item_cuantitativo_jurado (id, nombre, descripcion) VALUES (?, 'Documentacion', 'desc')")
                .setParameter(1, categoria).executeUpdate();
        entityManager.createNativeQuery("""
                INSERT INTO item_cuantitativo_jurado (id, nombre, descripcion, categoria_id, valor)
                VALUES (?, 'Rigor', 'desc', ?, 500)
                """)
                .setParameter(1, item).setParameter(2, categoria).executeUpdate();
        entityManager.createNativeQuery("""
                INSERT INTO evaluacion_cuantitativa_jurado (id, evaluacion_jurado_id, item_id, puntaje)
                VALUES (?, ?, ?, 100)
                """)
                .setParameter(1, evaluacionCuantitativa).setParameter(2, UUID.randomUUID())
                .setParameter(3, item).executeUpdate();
        entityManager.flush();
        entityManager.clear();
        return evaluacionCuantitativa;
    }

    @Test
    void debeRetornarVerdadero_cuandoLaEvaluacionCuantitativaExiste() {
        // Arrange
        var evaluacionCuantitativa = sembrarEvaluacionCuantitativa();

        // Act
        var existe = adapter.existePorId(evaluacionCuantitativa);

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void debeRetornarFalso_cuandoLaEvaluacionCuantitativaNoExiste() {
        // Act
        var existe = adapter.existePorId(UUID.randomUUID());

        // Assert
        assertThat(existe).isFalse();
    }
}
