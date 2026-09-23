package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroInvalidoException;
import com.arquisoft.shared.query.pagination.SortDirection;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// evaluacion, entregable y estado_evaluacion no tienen @Entity de comando, asi que se crean a mano
// con el mismo DDL de soporte que EvaluacionCualitativaJuradoQueryOutputAdapterTest: el CREATE TABLE
// IF NOT EXISTS es compartido entre clases via el mismo H2, asi que el esquema debe coincidir.
// Cada prueba siembra proyectos con un sufijo propio y filtra por el, para no depender de filas que
// otra clase haya dejado confirmadas por el commit implicito del DDL.
// @DirtiesContext: el CREATE TABLE nativo hace commit implicito en H2 y rompe el rollback
// transaccional de @DataJpaTest entre clases que comparten el mismo contexto cacheado.
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EvaluacionQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionQueryRepository repository;

    private EvaluacionQueryOutputAdapter adapter;

    private String sufijo;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionQueryOutputAdapter(repository, new EvaluacionJpaSpecification());
        sufijo = UUID.randomUUID().toString();
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
                CREATE TABLE IF NOT EXISTS evaluacion (
                    id UUID NOT NULL, entregable_id UUID NOT NULL,
                    estado_evaluacion_id VARCHAR(60) NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        sembrarEstado("PENDIENTE", "Pendiente");
        sembrarEstado("EN_PROGRESO", "En progreso");
        sembrarEstado("FINALIZADA", "Finalizada");
        entityManager.flush();
    }

    private void sembrarEstado(String id, String nombre) {
        entityManager.createNativeQuery(
                        "MERGE INTO estado_evaluacion (id, nombre, descripcion) VALUES (?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, nombre)
                .setParameter(3, nombre)
                .executeUpdate();
    }

    private UUID sembrarEvaluacion(UUID entregable, String proyecto, int version, String estado) {
        entityManager.createNativeQuery(
                        "INSERT INTO entregable (id, proyecto, version_entregable) VALUES (?, ?, ?)")
                .setParameter(1, entregable)
                .setParameter(2, proyecto + " " + sufijo)
                .setParameter(3, version)
                .executeUpdate();
        var evaluacion = UUID.randomUUID();
        entityManager.createNativeQuery(
                        "INSERT INTO evaluacion (id, entregable_id, estado_evaluacion_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacion)
                .setParameter(2, entregable)
                .setParameter(3, estado)
                .executeUpdate();
        entityManager.flush();
        return evaluacion;
    }

    private UUID sembrarEvaluacion(String proyecto, String estado) {
        return sembrarEvaluacion(UUID.randomUUID(), proyecto, 1, estado);
    }

    private NodoFiltro deEstaPrueba() {
        return NodoFiltro.predicado("proyecto", FiltroOperador.CONTIENE, sufijo);
    }

    private NodoFiltro deEstaPruebaY(NodoFiltro filtro) {
        return NodoFiltro.grupo(FiltroConector.AND, List.of(deEstaPrueba(), filtro));
    }

    @Test
    void debeMapearEntregableYEstado_desdeLosAliasDelSubselect() {
        // Arrange
        var entregable = UUID.randomUUID();
        var evaluacion = sembrarEvaluacion(entregable, "Robot seguidor", 2, "EN_PROGRESO");

        // Act
        var resultado = adapter.consultarTodas(EvaluacionCriteria.builder().raiz(deEstaPrueba()).build());

        // Assert
        assertThat(resultado.getContent()).containsExactly(new EvaluacionReadModel(
                evaluacion,
                new EvaluacionReadModel.Entregable(entregable, "Robot seguidor " + sufijo, 2),
                new EvaluacionReadModel.Estado("EN_PROGRESO", "En progreso")));
    }

    @Test
    void debeRetornarTodasLasEvaluaciones_cuandoNoHayFiltro() {
        // Arrange
        var primera = sembrarEvaluacion("Alfa", "PENDIENTE");
        var segunda = sembrarEvaluacion("Beta", "EN_PROGRESO");
        var tercera = sembrarEvaluacion("Gamma", "FINALIZADA");

        // Act
        var resultado = adapter.consultarTodas(EvaluacionCriteria.builder().tamanio(100).build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionReadModel::id)
                .contains(primera, segunda, tercera);
    }

    @Test
    void debeFiltrarPorEstadoConIn() {
        // Arrange
        var pendiente = sembrarEvaluacion("Alfa", "PENDIENTE");
        var enProgreso = sembrarEvaluacion("Beta", "EN_PROGRESO");
        sembrarEvaluacion("Gamma", "FINALIZADA");

        // Act
        var resultado = adapter.consultarTodas(EvaluacionCriteria.builder()
                .raiz(deEstaPruebaY(NodoFiltro.predicadoMultivalor(
                        "estado", FiltroOperador.IN, List.of("PENDIENTE", "EN_PROGRESO"))))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionReadModel::id)
                .containsExactlyInAnyOrder(pendiente, enProgreso);
    }

    @Test
    void debeFiltrarPorProyectoConContiene() {
        // Arrange
        var robot = sembrarEvaluacion("Brazo robotico", "PENDIENTE");
        sembrarEvaluacion("Sistema contable", "PENDIENTE");

        // Act
        var resultado = adapter.consultarTodas(EvaluacionCriteria.builder()
                .raiz(deEstaPruebaY(NodoFiltro.predicado("proyecto", FiltroOperador.CONTIENE, "robot")))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionReadModel::id)
                .containsExactly(robot);
    }

    @Test
    void debeFiltrarPorEntregableIdConEs() {
        // Arrange
        var entregable = UUID.randomUUID();
        var evaluacion = sembrarEvaluacion(entregable, "Alfa", 1, "PENDIENTE");
        sembrarEvaluacion("Beta", "PENDIENTE");

        // Act
        var resultado = adapter.consultarTodas(EvaluacionCriteria.builder()
                .raiz(NodoFiltro.predicado("entregableId", FiltroOperador.ES, entregable.toString()))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionReadModel::id)
                .containsExactly(evaluacion);
    }

    @Test
    void debeLanzarFiltroInvalidoException_cuandoEntregableIdNoEsUnUuid() {
        // Arrange
        var criteria = EvaluacionCriteria.builder()
                .raiz(NodoFiltro.predicado("entregableId", FiltroOperador.ES, "no-es-un-uuid"))
                .build();

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultarTodas(criteria))
                .isInstanceOf(FiltroInvalidoException.class);
    }

    @Test
    void debeOrdenarDescendentementePorNombreDeEstado() {
        // Arrange
        sembrarEvaluacion("Alfa", "EN_PROGRESO");
        sembrarEvaluacion("Beta", "FINALIZADA");
        sembrarEvaluacion("Gamma", "PENDIENTE");

        // Act
        var resultado = adapter.consultarTodas(EvaluacionCriteria.builder()
                .raiz(deEstaPrueba())
                .ordenamiento(List.of(SortOrder.of("estado", SortDirection.DESC)))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionReadModel::estado)
                .extracting(EvaluacionReadModel.Estado::nombre)
                .containsExactly("Pendiente", "Finalizada", "En progreso");
    }

    @Test
    void debePaginar_conElTotalDeLasCoincidencias() {
        // Arrange
        sembrarEvaluacion("Alfa", "PENDIENTE");
        sembrarEvaluacion("Beta", "PENDIENTE");
        sembrarEvaluacion("Gamma", "PENDIENTE");

        // Act
        var resultado = adapter.consultarTodas(EvaluacionCriteria.builder()
                .raiz(deEstaPrueba())
                .pagina(1)
                .tamanio(2)
                .ordenamiento(List.of(SortOrder.of("proyecto", SortDirection.ASC)))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionReadModel::entregable)
                .extracting(EvaluacionReadModel.Entregable::proyecto)
                .containsExactly("Gamma " + sufijo);
        assertThat(resultado.getTotalElements()).isEqualTo(3);
        assertThat(resultado.getTotalPages()).isEqualTo(2);
    }

    @Test
    void debeRetornarPaginaVacia_cuandoNoHayCoincidencias() {
        // Act
        var resultado = adapter.consultarTodas(EvaluacionCriteria.builder().raiz(deEstaPrueba()).build());

        // Assert
        assertThat(resultado.isEmpty()).isTrue();
        assertThat(resultado.getTotalElements()).isZero();
    }
}
