package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// evaluacion_jurado y evaluacion tienen @Entity de comando (Hibernate las crea); entregable y
// estado_evaluacion no, asi que se crean a mano con el DDL compartido por los demas tests del contexto.
// @DirtiesContext: el CREATE TABLE nativo hace commit implicito en H2 y rompe el rollback
// transaccional de @DataJpaTest entre clases que comparten el mismo contexto cacheado.
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EvaluacionAsignadaJuradoQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionAsignadaJuradoQueryRepository repository;

    private EvaluacionAsignadaJuradoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionAsignadaJuradoQueryOutputAdapter(repository);
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS entregable (
                    id UUID NOT NULL, proyecto VARCHAR(200) NOT NULL,
                    version_entregable INTEGER NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS estado_evaluacion (
                    id VARCHAR(60) NOT NULL, nombre VARCHAR(60) NOT NULL,
                    descripcion VARCHAR(300) NOT NULL, orden INTEGER, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery(
                "ALTER TABLE estado_evaluacion ADD COLUMN IF NOT EXISTS orden INTEGER").executeUpdate();
        sembrarEstado("PENDIENTE", "Pendiente", 1);
        sembrarEstado("EN_PROGRESO", "En progreso", 2);
        sembrarEstado("FINALIZADA", "Finalizada", 3);
        entityManager.flush();
    }

    private void sembrarEstado(String id, String nombre, int orden) {
        entityManager.createNativeQuery(
                        "MERGE INTO estado_evaluacion (id, nombre, descripcion, orden) VALUES (?, ?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, nombre)
                .setParameter(3, nombre)
                .setParameter(4, orden)
                .executeUpdate();
    }

    private UUID sembrarAsignacion(UUID jurado, UUID entregable, String proyecto, int version, String estado) {
        entityManager.createNativeQuery(
                        "INSERT INTO entregable (id, proyecto, version_entregable) VALUES (?, ?, ?)")
                .setParameter(1, entregable)
                .setParameter(2, proyecto)
                .setParameter(3, version)
                .executeUpdate();
        var evaluacion = UtilUUID.generarNuevoUUID();
        entityManager.createNativeQuery(
                        "INSERT INTO evaluacion (id, entregable_id, estado_evaluacion_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacion)
                .setParameter(2, entregable)
                .setParameter(3, estado)
                .executeUpdate();
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        entityManager.createNativeQuery(
                        "INSERT INTO evaluacion_jurado (id, evaluacion_id, jurado_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacionJurado)
                .setParameter(2, evaluacion)
                .setParameter(3, jurado)
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
        return evaluacionJurado;
    }

    private UUID sembrarAsignacion(UUID jurado, String proyecto, int version, String estado) {
        return sembrarAsignacion(jurado, UtilUUID.generarNuevoUUID(), proyecto, version, estado);
    }

    @Test
    void debeDevolverSoloAsignacionesDelJurado() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var propia = sembrarAsignacion(jurado, "Alfa", 1, "PENDIENTE");
        sembrarAsignacion(UtilUUID.generarNuevoUUID(), "Beta", 1, "PENDIENTE");

        // Act
        var resultado = adapter.consultar(new EvaluacionesAsignadasJuradoCriteria(jurado));

        // Assert
        assertThat(resultado).extracting(EvaluacionAsignadaJuradoReadModel::id).containsExactly(propia);
    }

    @Test
    void debeMapearEvaluacionEstadoYEntregable() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var evaluacionJurado = sembrarAsignacion(jurado, entregable, "Robot seguidor", 2, "EN_PROGRESO");

        // Act
        var resultado = adapter.consultar(new EvaluacionesAsignadasJuradoCriteria(jurado));

        // Assert
        assertThat(resultado).singleElement().satisfies(asignacion -> {
            assertThat(asignacion.id()).isEqualTo(evaluacionJurado);
            assertThat(asignacion.evaluacion().id()).isNotNull();
            assertThat(asignacion.evaluacion().estado())
                    .isEqualTo(new EvaluacionAsignadaJuradoReadModel.Estado("EN_PROGRESO", "En progreso"));
            assertThat(asignacion.entregable())
                    .isEqualTo(new EvaluacionAsignadaJuradoReadModel.Entregable(entregable, "Robot seguidor", 2));
        });
    }

    @Test
    void debeOrdenarPorEstadoAntesQueProyecto_conFinalizadaAlFinal() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var finalizada = sembrarAsignacion(jurado, "Alfa", 1, "FINALIZADA");
        var enProgreso = sembrarAsignacion(jurado, "Beta", 1, "EN_PROGRESO");
        var pendiente = sembrarAsignacion(jurado, "Gamma", 1, "PENDIENTE");

        // Act
        var resultado = adapter.consultar(new EvaluacionesAsignadasJuradoCriteria(jurado));

        // Assert
        assertThat(resultado)
                .extracting(EvaluacionAsignadaJuradoReadModel::id)
                .containsExactly(pendiente, enProgreso, finalizada);
    }

    @Test
    void debeDesempatarPorProyectoYVersion() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var betaV2 = sembrarAsignacion(jurado, "Beta", 2, "PENDIENTE");
        var alfaV1 = sembrarAsignacion(jurado, "Alfa", 1, "PENDIENTE");
        var betaV1 = sembrarAsignacion(jurado, "Beta", 1, "PENDIENTE");

        // Act
        var resultado = adapter.consultar(new EvaluacionesAsignadasJuradoCriteria(jurado));

        // Assert
        assertThat(resultado)
                .extracting(EvaluacionAsignadaJuradoReadModel::id)
                .containsExactly(alfaV1, betaV1, betaV2);
    }

    @Test
    void debeRetornarVacio_cuandoElJuradoNoTieneAsignaciones() {
        // Arrange
        sembrarAsignacion(UtilUUID.generarNuevoUUID(), "Alfa", 1, "PENDIENTE");

        // Act
        var resultado = adapter.consultar(new EvaluacionesAsignadasJuradoCriteria(UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(resultado).isEmpty();
    }
}
