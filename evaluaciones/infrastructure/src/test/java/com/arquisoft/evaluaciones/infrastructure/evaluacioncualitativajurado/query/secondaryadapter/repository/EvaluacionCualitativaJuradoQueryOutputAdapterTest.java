package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel.EvaluacionCualitativaJuradoReadModel;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// Las tablas jurado/entregable/estado_evaluacion/evaluacion/evaluacion_jurado/
// evaluacion_cualitativa_jurado/criterio_item_cualitativo_jurado no tienen @Entity en el lado de
// comando todavia (esta HU es de solo lectura), asi que Hibernate no las genera con ddl-auto. Se
// crean aqui con el mismo DDL de las migraciones V20260906143120/V20260831121852 para poder
// ejercitar el @Subselect real de siete tablas, tal como exige la skill de testing.
@DataJpaTest
class EvaluacionCualitativaJuradoQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionCualitativaJuradoQueryRepository repository;

    private EvaluacionCualitativaJuradoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionCualitativaJuradoQueryOutputAdapter(repository);
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
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS criterio_item_cualitativo_jurado (
                    id UUID NOT NULL, nombre VARCHAR(100) NOT NULL,
                    descripcion VARCHAR(300) NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS evaluacion_cualitativa_jurado (
                    id UUID NOT NULL, evaluacion_jurado_id UUID NOT NULL,
                    item_id UUID NOT NULL, criterio_id UUID NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.flush();
    }

    private UUID sembrarJurado() {
        UUID jurado = UUID.randomUUID();
        entityManager.createNativeQuery(
                "INSERT INTO jurado (id, identificador, nombre, email) VALUES (?, 'DOC-1', 'Jurado Uno', 'j1@uco.edu.co')")
                .setParameter(1, jurado).executeUpdate();
        return jurado;
    }

    private UUID sembrarAccesoEstudiante(UUID estudiante, boolean activo) {
        UUID entregable = UUID.randomUUID();
        UUID proyecto = UUID.randomUUID();
        entityManager.createNativeQuery(
                "INSERT INTO entregable (id, proyecto, version_entregable) VALUES (?, 'Proyecto', 1)")
                .setParameter(1, entregable).executeUpdate();
        entityManager.createNativeQuery("""
                INSERT INTO entregable_proyecto_acceso
                    (entregable_id, proyecto_id, version_entregable, activo, ocurrido_en)
                VALUES (?, ?, 1, true, ?)
                """)
                .setParameter(1, entregable).setParameter(2, proyecto).setParameter(3, Instant.now())
                .executeUpdate();
        entityManager.createNativeQuery("""
                INSERT INTO proyecto_estudiante_acceso (proyecto_id, estudiante_id, activo, ocurrido_en)
                VALUES (?, ?, ?, ?)
                """)
                .setParameter(1, proyecto).setParameter(2, estudiante)
                .setParameter(3, activo).setParameter(4, Instant.now())
                .executeUpdate();
        return entregable;
    }

    private UUID sembrarEvaluacionJurado(UUID jurado, UUID entregable) {
        UUID evaluacion = UUID.randomUUID();
        UUID evaluacionJurado = UUID.randomUUID();
        entityManager.createNativeQuery(
                "INSERT INTO evaluacion (id, entregable_id, estado_evaluacion_id) VALUES (?, ?, 'PENDIENTE')")
                .setParameter(1, evaluacion).setParameter(2, entregable).executeUpdate();
        entityManager.createNativeQuery(
                "INSERT INTO evaluacion_jurado (id, evaluacion_id, jurado_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacionJurado).setParameter(2, evaluacion).setParameter(3, jurado)
                .executeUpdate();
        return evaluacionJurado;
    }

    private void sembrarItem(UUID id, String nombre, String descripcion) {
        entityManager.createNativeQuery(
                "INSERT INTO item_cualitativo_jurado (id, nombre, descripcion) VALUES (?, ?, ?)")
                .setParameter(1, id).setParameter(2, nombre).setParameter(3, descripcion)
                .executeUpdate();
    }

    private void sembrarCriterio(UUID id, String nombre, String descripcion) {
        entityManager.createNativeQuery(
                "INSERT INTO criterio_item_cualitativo_jurado (id, nombre, descripcion) VALUES (?, ?, ?)")
                .setParameter(1, id).setParameter(2, nombre).setParameter(3, descripcion)
                .executeUpdate();
    }

    private void sembrarEvaluacionCualitativa(UUID id, UUID evaluacionJurado, UUID item, UUID criterio) {
        entityManager.createNativeQuery("""
                INSERT INTO evaluacion_cualitativa_jurado (id, evaluacion_jurado_id, item_id, criterio_id)
                VALUES (?, ?, ?, ?)
                """)
                .setParameter(1, id).setParameter(2, evaluacionJurado)
                .setParameter(3, item).setParameter(4, criterio)
                .executeUpdate();
    }

    @Test
    void debeRetornarLasEvaluacionesCualitativas_conElItemYElCriterioMapeados() {
        // Arrange
        UUID estudiante = UUID.randomUUID();
        UUID jurado = sembrarJurado();
        UUID entregable = sembrarAccesoEstudiante(estudiante, true);
        UUID evaluacionJurado = sembrarEvaluacionJurado(jurado, entregable);

        UUID item = UUID.randomUUID();
        UUID criterio = UUID.randomUUID();
        sembrarItem(item, "Claridad", "Evalúa la claridad conceptual");
        sembrarCriterio(criterio, "Excelente", "Cumple todos los aspectos");
        UUID evaluacionCualitativa = UUID.randomUUID();
        sembrarEvaluacionCualitativa(evaluacionCualitativa, evaluacionJurado, item, criterio);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionCualitativaJuradoReadModel> resultado =
                adapter.consultar(new EvaluacionCualitativaJuradoCriteria(evaluacionJurado, estudiante));

        // Assert
        assertThat(resultado).hasSize(1);
        EvaluacionCualitativaJuradoReadModel readModel = resultado.get(0);
        assertThat(readModel.id()).isEqualTo(evaluacionCualitativa);
        assertThat(readModel.item().id()).isEqualTo(item);
        assertThat(readModel.item().nombre()).isEqualTo("Claridad");
        assertThat(readModel.item().descripcion()).isEqualTo("Evalúa la claridad conceptual");
        assertThat(readModel.criterio().id()).isEqualTo(criterio);
        assertThat(readModel.criterio().nombre()).isEqualTo("Excelente");
        assertThat(readModel.criterio().descripcion()).isEqualTo("Cumple todos los aspectos");
    }

    @Test
    void debeRetornarListaVacia_cuandoLaEvaluacionJuradoNoTieneEvaluacionesCualitativas() {
        // Arrange
        UUID estudiante = UUID.randomUUID();
        UUID jurado = sembrarJurado();
        UUID entregable = sembrarAccesoEstudiante(estudiante, true);
        UUID evaluacionJurado = sembrarEvaluacionJurado(jurado, entregable);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionCualitativaJuradoReadModel> resultado =
                adapter.consultar(new EvaluacionCualitativaJuradoCriteria(evaluacionJurado, estudiante));

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void noDebeRetornarNada_cuandoElEstudianteEsAjenoAlProyecto() {
        // Arrange
        UUID propietario = UUID.randomUUID();
        UUID ajeno = UUID.randomUUID();
        UUID jurado = sembrarJurado();
        UUID entregable = sembrarAccesoEstudiante(propietario, true);
        UUID evaluacionJurado = sembrarEvaluacionJurado(jurado, entregable);

        UUID item = UUID.randomUUID();
        UUID criterio = UUID.randomUUID();
        sembrarItem(item, "Claridad", "desc");
        sembrarCriterio(criterio, "Excelente", "desc");
        sembrarEvaluacionCualitativa(UUID.randomUUID(), evaluacionJurado, item, criterio);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionCualitativaJuradoReadModel> resultado =
                adapter.consultar(new EvaluacionCualitativaJuradoCriteria(evaluacionJurado, ajeno));

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeOrdenarPorNombreDelItemYLuegoPorId_deFormaDeterminista() {
        // Arrange
        UUID estudiante = UUID.randomUUID();
        UUID jurado = sembrarJurado();
        UUID entregable = sembrarAccesoEstudiante(estudiante, true);
        UUID evaluacionJurado = sembrarEvaluacionJurado(jurado, entregable);

        UUID itemRigor = UUID.randomUUID();
        UUID itemClaridad = UUID.randomUUID();
        UUID criterio = UUID.randomUUID();
        sembrarItem(itemRigor, "Rigor", "desc");
        sembrarItem(itemClaridad, "Claridad", "desc");
        sembrarCriterio(criterio, "Excelente", "desc");
        sembrarEvaluacionCualitativa(UUID.randomUUID(), evaluacionJurado, itemRigor, criterio);
        sembrarEvaluacionCualitativa(UUID.randomUUID(), evaluacionJurado, itemClaridad, criterio);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionCualitativaJuradoReadModel> resultado =
                adapter.consultar(new EvaluacionCualitativaJuradoCriteria(evaluacionJurado, estudiante));

        // Assert
        assertThat(resultado).extracting(r -> r.item().nombre())
                .containsExactly("Claridad", "Rigor");
    }
}
