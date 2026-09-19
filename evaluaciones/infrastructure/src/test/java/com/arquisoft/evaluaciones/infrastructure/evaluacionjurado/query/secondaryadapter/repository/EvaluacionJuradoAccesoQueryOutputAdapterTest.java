package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// La tabla evaluacion_jurado no tiene @Entity en el lado de comando (aun no existe esa HU de
// escritura), asi que Hibernate no la genera con ddl-auto. Se crea aqui con el mismo DDL de la
// migracion V20260906143120 para poder ejercitar el @Subselect real, tal como exige la skill de
// testing (nada de mockear el repository en un @Subselect).
@DataJpaTest
class EvaluacionJuradoAccesoQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionJuradoAccesoQueryRepository repository;

    private EvaluacionJuradoAccesoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionJuradoAccesoQueryOutputAdapter(repository);
        crearEsquemaDeSoporte();
    }

    private void crearEsquemaDeSoporte() {
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS evaluacion_jurado (
                    id UUID NOT NULL, evaluacion_id UUID NOT NULL, jurado_id UUID NOT NULL,
                    PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.flush();
    }

    private UUID sembrarEvaluacionJurado() {
        UUID evaluacionJurado = UUID.randomUUID();

        entityManager.createNativeQuery(
                "INSERT INTO evaluacion_jurado (id, evaluacion_id, jurado_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacionJurado)
                .setParameter(2, UUID.randomUUID())
                .setParameter(3, UUID.randomUUID())
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
        return evaluacionJurado;
    }

    @Test
    void debeRetornarTrue_cuandoLaEvaluacionJuradoExiste() {
        // Arrange
        UUID evaluacionJurado = sembrarEvaluacionJurado();

        // Act & Assert
        assertThat(adapter.existePorId(evaluacionJurado)).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoLaEvaluacionJuradoNoExiste() {
        // Act & Assert
        assertThat(adapter.existePorId(UUID.randomUUID())).isFalse();
    }
}
