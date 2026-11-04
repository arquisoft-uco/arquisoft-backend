package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// La tabla evaluacion no tiene @Entity en el lado de comando (ninguna HU de escritura la persiste
// aun), asi que Hibernate no la genera con ddl-auto. Se crea aqui con el mismo DDL de soporte que
// ya usan otros @DataJpaTest del contexto (EvaluacionJuradoCommandOutputAdapterTest,
// EvaluacionCualitativaJuradoQueryOutputAdapterTest): el CREATE TABLE IF NOT EXISTS es compartido
// entre clases via el mismo H2 de la sesion de test, asi que el esquema debe coincidir con el mas
// completo para no dejar sin crear columnas NOT NULL que otra clase si necesita.
// @DirtiesContext: el CREATE TABLE nativo hace commit implicito en H2 y rompe el rollback
// transaccional de @DataJpaTest entre clases que comparten el mismo contexto cacheado — sin esto,
// las filas sembradas aqui sobreviven y contaminan la clase que arranque despues en el mismo H2.
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EvaluacionAccesoQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionAccesoQueryRepository repository;

    private EvaluacionAccesoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionAccesoQueryOutputAdapter(repository);
        crearEsquemaDeSoporte();
    }

    private void crearEsquemaDeSoporte() {
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS evaluacion (
                    id UUID NOT NULL, entregable_id UUID NOT NULL,
                    estado_evaluacion_id VARCHAR(60) NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.flush();
    }

    private UUID sembrarEvaluacion() {
        var evaluacion = UUID.randomUUID();
        entityManager.createNativeQuery(
                "INSERT INTO evaluacion (id, entregable_id, estado_evaluacion_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacion)
                .setParameter(2, UUID.randomUUID())
                .setParameter(3, "PENDIENTE")
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
        return evaluacion;
    }

    @Test
    void debeRetornarTrue_cuandoLaEvaluacionExiste() {
        // Arrange
        var evaluacion = sembrarEvaluacion();

        // Act & Assert
        assertThat(adapter.existePorId(evaluacion)).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoLaEvaluacionNoExiste() {
        // Act & Assert
        assertThat(adapter.existePorId(UUID.randomUUID())).isFalse();
    }
}
