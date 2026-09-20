package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// Las tablas categoria_item_cuantitativo_jurado/item_cuantitativo_jurado/evaluacion_cuantitativa_jurado
// no tienen @Entity en el lado de comando todavia (esta HU es de solo lectura), asi que Hibernate no
// las genera con ddl-auto. Se crean aqui con el mismo DDL de las migraciones
// V20260906183010/V20260912224943 para poder ejercitar el @Subselect real de dos tablas, tal como
// exige la skill de testing.
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
        UUID evaluacionJurado = UUID.randomUUID();
        UUID categoria = sembrarCategoria("Documentacion");
        UUID item = UUID.randomUUID();
        sembrarItem(item, "Rigor", "Evalúa el rigor metodológico", categoria, 500);
        UUID evaluacionCuantitativa = UUID.randomUUID();
        sembrarEvaluacionCuantitativa(evaluacionCuantitativa, evaluacionJurado, item, 350);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionCuantitativaJuradoReadModel> resultado =
                adapter.consultar(new EvaluacionCuantitativaJuradoCriteria(evaluacionJurado));

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
        // Act
        List<EvaluacionCuantitativaJuradoReadModel> resultado =
                adapter.consultar(new EvaluacionCuantitativaJuradoCriteria(UUID.randomUUID()));

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void soloDebeRetornarLasEvaluacionesDeLaEvaluacionJuradoSolicitada() {
        // Arrange
        UUID solicitada = UUID.randomUUID();
        UUID otra = UUID.randomUUID();
        UUID categoria = sembrarCategoria("Documentacion");
        UUID item = UUID.randomUUID();
        sembrarItem(item, "Rigor", "desc", categoria, 500);
        UUID evaluacionSolicitada = UUID.randomUUID();
        sembrarEvaluacionCuantitativa(evaluacionSolicitada, solicitada, item, 200);
        sembrarEvaluacionCuantitativa(UUID.randomUUID(), otra, item, 100);
        entityManager.flush();
        entityManager.clear();

        // Act
        List<EvaluacionCuantitativaJuradoReadModel> resultado =
                adapter.consultar(new EvaluacionCuantitativaJuradoCriteria(solicitada));

        // Assert
        assertThat(resultado).extracting(EvaluacionCuantitativaJuradoReadModel::id)
                .containsExactly(evaluacionSolicitada);
    }

    @Test
    void debeOrdenarPorNombreDelItemYLuegoPorId_deFormaDeterminista() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
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
                adapter.consultar(new EvaluacionCuantitativaJuradoCriteria(evaluacionJurado));

        // Assert
        assertThat(resultado).extracting(r -> r.item().nombre())
                .containsExactly("Claridad", "Rigor");
    }

    @Test
    void debeRetornarVerdadero_cuandoLaEvaluacionCuantitativaExiste() {
        // Arrange
        var categoria = sembrarCategoria("Documentacion");
        var item = UUID.randomUUID();
        sembrarItem(item, "Rigor", "desc", categoria, 500);
        var evaluacionCuantitativa = UUID.randomUUID();
        sembrarEvaluacionCuantitativa(evaluacionCuantitativa, UUID.randomUUID(), item, 100);
        entityManager.flush();
        entityManager.clear();

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
