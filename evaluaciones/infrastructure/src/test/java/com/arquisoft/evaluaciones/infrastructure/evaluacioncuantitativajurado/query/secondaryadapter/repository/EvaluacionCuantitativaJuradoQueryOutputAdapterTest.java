package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
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
// categoria_item_cuantitativo_jurado/item_cuantitativo_jurado/evaluacion_cuantitativa_jurado no
// tienen @Entity en el lado de comando todavia (esta HU es de solo lectura), asi que Hibernate no
// las genera con ddl-auto. Se crean aqui con el mismo DDL de las migraciones
// V20260906183010/V20260912224943 para poder ejercitar el @Subselect real de seis tablas, tal
// como exige la skill de testing.
@DataJpaTest
class EvaluacionCuantitativaJuradoQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionCuantitativaJuradoQueryRepository repository;

    private EvaluacionCuantitativaJuradoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionCuantitativaJuradoQueryOutputAdapter(repository);
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

    private UUID sembrarCategoria(String nombre) {
        UUID categoria = UUID.randomUUID();
        entityManager.createNativeQuery(
                "INSERT INTO categoria_item_cuantitativo_jurado (id, nombre, descripcion) VALUES (?, ?, 'desc')")
                .setParameter(1, categoria).setParameter(2, nombre).executeUpdate();
        return categoria;
    }

    private void sembrarItem(UUID id, String nombre, String descripcion, UUID categoria, int valor) {
        entityManager.createNativeQuery("""
                INSERT INTO item_cuantitativo_jurado (id, nombre, descripcion, categoria_id, valor)
                VALUES (?, ?, ?, ?, ?)
                """)
                .setParameter(1, id).setParameter(2, nombre).setParameter(3, descripcion)
                .setParameter(4, categoria).setParameter(5, valor)
                .executeUpdate();
    }

    private void sembrarEvaluacionCuantitativa(UUID id, UUID evaluacionJurado, UUID item, int puntaje) {
        entityManager.createNativeQuery("""
                INSERT INTO evaluacion_cuantitativa_jurado (id, evaluacion_jurado_id, item_id, puntaje)
                VALUES (?, ?, ?, ?)
                """)
                .setParameter(1, id).setParameter(2, evaluacionJurado)
                .setParameter(3, item).setParameter(4, puntaje)
                .executeUpdate();
    }

    @Test
    void debeRetornarLasEvaluacionesCuantitativas_conElItemMapeadoIncluyendoCategoriaYValor() {
        // Arrange
        UUID estudiante = UUID.randomUUID();
        UUID jurado = sembrarJurado();
        UUID entregable = sembrarAccesoEstudiante(estudiante, true);
        UUID evaluacionJurado = sembrarEvaluacionJurado(jurado, entregable);

        UUID categoria = sembrarCategoria("Documentacion");
        UUID item = UUID.randomUUID();
        sembrarItem(item, "Rigor", "Evalúa el rigor metodológico", categoria, 500);
        UUID evaluacionCuantitativa = UUID.randomUUID();
        sembrarEvaluacionCuantitativa(evaluacionCuantitativa, evaluacionJurado, item, 350);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionCuantitativaJuradoReadModel> resultado =
                adapter.consultar(new EvaluacionCuantitativaJuradoCriteria(evaluacionJurado, estudiante));

        // Assert
        assertThat(resultado).hasSize(1);
        EvaluacionCuantitativaJuradoReadModel readModel = resultado.get(0);
        assertThat(readModel.id()).isEqualTo(evaluacionCuantitativa);
        assertThat(readModel.puntaje()).isEqualTo(350);
        assertThat(readModel.item().id()).isEqualTo(item);
        assertThat(readModel.item().nombre()).isEqualTo("Rigor");
        assertThat(readModel.item().descripcion()).isEqualTo("Evalúa el rigor metodológico");
        assertThat(readModel.item().categoriaId()).isEqualTo(categoria);
        assertThat(readModel.item().valor()).isEqualTo(500);
    }

    @Test
    void debeRetornarListaVacia_cuandoLaEvaluacionJuradoNoTieneEvaluacionesCuantitativas() {
        // Arrange
        UUID estudiante = UUID.randomUUID();
        UUID jurado = sembrarJurado();
        UUID entregable = sembrarAccesoEstudiante(estudiante, true);
        UUID evaluacionJurado = sembrarEvaluacionJurado(jurado, entregable);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionCuantitativaJuradoReadModel> resultado =
                adapter.consultar(new EvaluacionCuantitativaJuradoCriteria(evaluacionJurado, estudiante));

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

        UUID categoria = sembrarCategoria("Documentacion");
        UUID item = UUID.randomUUID();
        sembrarItem(item, "Rigor", "desc", categoria, 500);
        sembrarEvaluacionCuantitativa(UUID.randomUUID(), evaluacionJurado, item, 200);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionCuantitativaJuradoReadModel> resultado =
                adapter.consultar(new EvaluacionCuantitativaJuradoCriteria(evaluacionJurado, ajeno));

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

        UUID categoria = sembrarCategoria("Documentacion");
        UUID itemRigor = UUID.randomUUID();
        UUID itemClaridad = UUID.randomUUID();
        sembrarItem(itemRigor, "Rigor", "desc", categoria, 500);
        sembrarItem(itemClaridad, "Claridad", "desc", categoria, 500);
        sembrarEvaluacionCuantitativa(UUID.randomUUID(), evaluacionJurado, itemRigor, 100);
        sembrarEvaluacionCuantitativa(UUID.randomUUID(), evaluacionJurado, itemClaridad, 200);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionCuantitativaJuradoReadModel> resultado =
                adapter.consultar(new EvaluacionCuantitativaJuradoCriteria(evaluacionJurado, estudiante));

        // Assert
        assertThat(resultado).extracting(r -> r.item().nombre())
                .containsExactly("Claridad", "Rigor");
    }
}
