package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// Las tablas evaluacion_jurado/evaluacion/entregable/jurado/estado_evaluacion no tienen
// @Entity en el lado de comando (aun no existe esa HU de escritura), asi que Hibernate no las
// genera con ddl-auto. Se crean aqui con el mismo DDL de la migracion V20260906143120 para poder
// ejercitar el @Query nativo real, tal como exige la skill de testing (nada de mockear el
// repository en un @Subselect/@Query nativo).
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
                CREATE TABLE IF NOT EXISTS jurado (
                    id UUID NOT NULL, identificador VARCHAR(30) NOT NULL,
                    nombre VARCHAR(50) NOT NULL, email VARCHAR(50) NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS entregable (
                    id UUID NOT NULL, proyecto VARCHAR(200) NOT NULL,
                    version_entregable INTEGER NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
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
                CREATE TABLE IF NOT EXISTS evaluacion (
                    id UUID NOT NULL, entregable_id UUID NOT NULL,
                    estado_evaluacion_id VARCHAR(60) NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS evaluacion_jurado (
                    id UUID NOT NULL, evaluacion_id UUID NOT NULL, jurado_id UUID NOT NULL,
                    PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.flush();
    }

    private UUID sembrarEvaluacionJurado(UUID proyecto, UUID estudiante, boolean entregableActivo,
            boolean membresiaActiva) {
        UUID jurado = UUID.randomUUID();
        UUID entregable = UUID.randomUUID();
        UUID evaluacion = UUID.randomUUID();
        UUID evaluacionJurado = UUID.randomUUID();

        entityManager.createNativeQuery(
                "INSERT INTO jurado (id, identificador, nombre, email) VALUES (?, 'DOC-1', 'Jurado Uno', 'j1@uco.edu.co')")
                .setParameter(1, jurado).executeUpdate();
        entityManager.createNativeQuery(
                "INSERT INTO entregable (id, proyecto, version_entregable) VALUES (?, 'Proyecto', 1)")
                .setParameter(1, entregable).executeUpdate();
        entityManager.createNativeQuery(
                "INSERT INTO evaluacion (id, entregable_id, estado_evaluacion_id) VALUES (?, ?, 'PENDIENTE')")
                .setParameter(1, evaluacion).setParameter(2, entregable).executeUpdate();
        entityManager.createNativeQuery(
                "INSERT INTO evaluacion_jurado (id, evaluacion_id, jurado_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacionJurado).setParameter(2, evaluacion).setParameter(3, jurado)
                .executeUpdate();
        entityManager.createNativeQuery("""
                INSERT INTO entregable_proyecto_acceso
                    (entregable_id, proyecto_id, version_entregable, activo, ocurrido_en)
                VALUES (?, ?, 1, ?, ?)
                """)
                .setParameter(1, entregable).setParameter(2, proyecto)
                .setParameter(3, entregableActivo).setParameter(4, Instant.now())
                .executeUpdate();
        entityManager.createNativeQuery("""
                INSERT INTO proyecto_estudiante_acceso (proyecto_id, estudiante_id, activo, ocurrido_en)
                VALUES (?, ?, ?, ?)
                """)
                .setParameter(1, proyecto).setParameter(2, estudiante)
                .setParameter(3, membresiaActiva).setParameter(4, Instant.now())
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
        return evaluacionJurado;
    }

    @Test
    void debeRetornarTrue_cuandoLaEvaluacionJuradoExiste() {
        // Arrange
        UUID evaluacionJurado = sembrarEvaluacionJurado(UUID.randomUUID(), UUID.randomUUID(), true, true);

        // Act & Assert
        assertThat(adapter.existePorId(evaluacionJurado)).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoLaEvaluacionJuradoNoExiste() {
        // Act & Assert
        assertThat(adapter.existePorId(UUID.randomUUID())).isFalse();
    }

    @Test
    void debePertenecer_cuandoElEstudianteTieneAccesoActivoAlEntregableYAlProyecto() {
        // Arrange
        UUID proyecto = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        UUID evaluacionJurado = sembrarEvaluacionJurado(proyecto, estudiante, true, true);

        // Act & Assert
        assertThat(adapter.perteneceAlEstudiante(evaluacionJurado, estudiante)).isTrue();
    }

    @Test
    void noDebePertenecer_cuandoLaMembresiaDelEstudianteEstaInactiva() {
        // Arrange
        UUID proyecto = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        UUID evaluacionJurado = sembrarEvaluacionJurado(proyecto, estudiante, true, false);

        // Act & Assert
        assertThat(adapter.perteneceAlEstudiante(evaluacionJurado, estudiante)).isFalse();
    }

    @Test
    void noDebePertenecer_cuandoElEstudianteEsAjenoAlProyecto() {
        // Arrange
        UUID proyecto = UUID.randomUUID();
        UUID evaluacionJurado = sembrarEvaluacionJurado(proyecto, UUID.randomUUID(), true, true);

        // Act & Assert
        assertThat(adapter.perteneceAlEstudiante(evaluacionJurado, UUID.randomUUID())).isFalse();
    }
}
