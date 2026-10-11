package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// Las tablas evaluacion/estado_evaluacion no tienen @Entity en el lado de comando (esta HU solo lee
// el jurado y el estado, no gestiona EvaluacionJurado/Evaluacion como recursos propios), asi que
// Hibernate no las genera con ddl-auto. Se crean aqui con el mismo DDL de la migracion
// V20260906143120 para poder ejercitar el @Query nativo real con JOIN, tal como exige la skill de
// testing (nada de mockear el repository en una consulta nativa).
@DataJpaTest
class EvaluacionJuradoCommandOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionJuradoCommandRepository repository;

    private EvaluacionJuradoCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionJuradoCommandOutputAdapter(repository);
        crearEsquemaDeSoporte();
    }

    private void crearEsquemaDeSoporte() {
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS estado_evaluacion (
                    id VARCHAR(60) NOT NULL, nombre VARCHAR(60) NOT NULL,
                    descripcion VARCHAR(300) NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                MERGE INTO estado_evaluacion (id, nombre, descripcion)
                VALUES ('PENDIENTE', 'Pendiente', 'Pendiente por realizar')
                """).executeUpdate();
        entityManager.createNativeQuery("""
                MERGE INTO estado_evaluacion (id, nombre, descripcion)
                VALUES ('FINALIZADA', 'Finalizada', 'Evaluacion finalizada')
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS evaluacion (
                    id UUID NOT NULL, entregable_id UUID NOT NULL,
                    estado_evaluacion_id VARCHAR(60) NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.flush();
    }

    private UUID sembrarEvaluacionJurado(UUID jurado, String estadoEvaluacion) {
        UUID evaluacion = UUID.randomUUID();
        UUID evaluacionJurado = UUID.randomUUID();
        entityManager.createNativeQuery(
                "INSERT INTO evaluacion (id, entregable_id, estado_evaluacion_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacion).setParameter(2, UUID.randomUUID())
                .setParameter(3, estadoEvaluacion).executeUpdate();
        entityManager.createNativeQuery(
                "INSERT INTO evaluacion_jurado (id, evaluacion_id, jurado_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacionJurado).setParameter(2, evaluacion).setParameter(3, jurado)
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
        return evaluacionJurado;
    }

    // H2 entrega las columnas uuid de una consulta nativa como byte[] (PostgreSQL las entrega como
    // UUID), asi que aqui se ejercita la consulta real por su columna de texto y el mapeo de los
    // identificadores se cubre en EstadoEvaluacionJuradoJpaMapperTest.
    @Test
    void debeLeerElEstadoDeLaEvaluacion_cuandoLaEvaluacionJuradoExiste() {
        // Arrange
        UUID evaluacionJurado = sembrarEvaluacionJurado(UUID.randomUUID(), "PENDIENTE");

        // Act
        var estado = repository.buscarEstadoBloqueado(evaluacionJurado);

        // Assert
        assertThat(estado).hasValueSatisfying(encontrado ->
                assertThat(encontrado.getEstado()).isEqualTo("PENDIENTE"));
    }

    @Test
    void debeLeerEstadoFinalizada_cuandoLaEvaluacionEstaFinalizada() {
        // Arrange
        UUID evaluacionJurado = sembrarEvaluacionJurado(UUID.randomUUID(), "FINALIZADA");

        // Act
        var estado = repository.buscarEstadoBloqueado(evaluacionJurado);

        // Assert
        assertThat(estado).hasValueSatisfying(encontrado ->
                assertThat(encontrado.getEstado()).isEqualTo("FINALIZADA"));
    }

    @Test
    void debeRetornarVacio_cuandoLaEvaluacionJuradoNoExiste() {
        // Act & Assert
        assertThat(adapter.obtenerEstadoBloqueado(UUID.randomUUID())).isEmpty();
    }
}
