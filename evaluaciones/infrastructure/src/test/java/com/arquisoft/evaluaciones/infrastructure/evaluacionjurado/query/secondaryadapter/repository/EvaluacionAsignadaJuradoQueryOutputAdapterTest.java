package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// evaluacion_jurado y evaluacion tienen @Entity de comando (Hibernate las crea); entregable,
// estado_evaluacion y estado_evaluacion_asignada_jurado no, asi que se crean a mano con el DDL compartido por
// los demas tests del contexto.
// @DirtiesContext: el CREATE TABLE nativo hace commit implicito en H2 y rompe el rollback
// transaccional de @DataJpaTest entre clases que comparten el mismo contexto cacheado.
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EvaluacionAsignadaJuradoQueryOutputAdapterTest {

    private static final String PENDIENTE_POR_EVALUAR = "PENDIENTE_POR_EVALUAR";
    private static final String EVALUADA = "EVALUADA";

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionAsignadaJuradoQueryRepository repository;

    private EvaluacionAsignadaJuradoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionAsignadaJuradoQueryOutputAdapter(
                repository, new EvaluacionAsignadaJuradoJpaSpecification());
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
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS estado_evaluacion_asignada_jurado (
                    id VARCHAR(60) NOT NULL, nombre VARCHAR(60) NOT NULL,
                    descripcion VARCHAR(300) NOT NULL, orden INTEGER NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        sembrarEstado("estado_evaluacion", "PENDIENTE", "Pendiente", 1);
        sembrarEstado("estado_evaluacion", "EN_PROGRESO", "En progreso", 2);
        sembrarEstado("estado_evaluacion", "FINALIZADA", "Finalizada", 3);
        sembrarEstado("estado_evaluacion_asignada_jurado", PENDIENTE_POR_EVALUAR, "Pendiente por evaluar", 1);
        sembrarEstado("estado_evaluacion_asignada_jurado", EVALUADA, "Evaluada", 2);
        entityManager.flush();
    }

    private void sembrarEstado(String tabla, String id, String nombre, int orden) {
        entityManager.createNativeQuery(
                        "MERGE INTO " + tabla + " (id, nombre, descripcion, orden) VALUES (?, ?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, nombre)
                .setParameter(3, nombre)
                .setParameter(4, orden)
                .executeUpdate();
    }

    private UUID sembrarAsignacion(
            UUID jurado, UUID entregable, String proyecto, int version, String estado, String estadoJurado) {
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
        entityManager.createNativeQuery("""
                        INSERT INTO evaluacion_jurado (id, evaluacion_id, jurado_id, estado_evaluacion_asignada_jurado_id)
                        VALUES (?, ?, ?, ?)
                        """)
                .setParameter(1, evaluacionJurado)
                .setParameter(2, evaluacion)
                .setParameter(3, jurado)
                .setParameter(4, estadoJurado)
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
        return evaluacionJurado;
    }

    private UUID sembrarAsignacion(UUID jurado, String proyecto, int version, String estado, String estadoJurado) {
        return sembrarAsignacion(jurado, UtilUUID.generarNuevoUUID(), proyecto, version, estado, estadoJurado);
    }

    private UUID sembrarAsignacion(UUID jurado, String proyecto, int version, String estado) {
        return sembrarAsignacion(jurado, proyecto, version, estado, PENDIENTE_POR_EVALUAR);
    }

    private void sembrarEvaluacionCualitativa(UUID evaluacionJurado) {
        entityManager.createNativeQuery("""
                        INSERT INTO evaluacion_cualitativa_jurado (id, evaluacion_jurado_id, item_id, criterio_id)
                        VALUES (?, ?, ?, ?)
                        """)
                .setParameter(1, UtilUUID.generarNuevoUUID())
                .setParameter(2, evaluacionJurado)
                .setParameter(3, UtilUUID.generarNuevoUUID())
                .setParameter(4, UtilUUID.generarNuevoUUID())
                .executeUpdate();
    }

    private void sembrarEvaluacionCuantitativa(UUID evaluacionJurado) {
        entityManager.createNativeQuery("""
                        INSERT INTO evaluacion_cuantitativa_jurado (id, evaluacion_jurado_id, item_id, puntaje)
                        VALUES (?, ?, ?, ?)
                        """)
                .setParameter(1, UtilUUID.generarNuevoUUID())
                .setParameter(2, evaluacionJurado)
                .setParameter(3, UtilUUID.generarNuevoUUID())
                .setParameter(4, 4)
                .executeUpdate();
    }

    private static EvaluacionesAsignadasJuradoCriteria.Builder criteria(UUID jurado) {
        return EvaluacionesAsignadasJuradoCriteria.builder().jurado(jurado);
    }

    @Test
    void debeDevolverSoloAsignacionesDelJurado() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var propia = sembrarAsignacion(jurado, "Alfa", 1, "PENDIENTE");
        sembrarAsignacion(UtilUUID.generarNuevoUUID(), "Beta", 1, "PENDIENTE");

        // Act
        var resultado = adapter.consultarTodas(criteria(jurado).build());

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent()).extracting(EvaluacionAsignadaJuradoReadModel::id).containsExactly(propia);
    }

    @Test
    void debeMapearEstadoJuradoEvaluacionYEntregable() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var evaluacionJurado = sembrarAsignacion(
                jurado, entregable, "Robot seguidor", 2, "EN_PROGRESO", EVALUADA);

        // Act
        var resultado = adapter.consultarTodas(criteria(jurado).build());

        // Assert
        assertThat(resultado.getContent()).singleElement().satisfies(asignacion -> {
            assertThat(asignacion.id()).isEqualTo(evaluacionJurado);
            assertThat(asignacion.estado())
                    .isEqualTo(new EvaluacionAsignadaJuradoReadModel.Estado(EVALUADA, "Evaluada"));
            assertThat(asignacion.evaluacion().id()).isNotNull();
            assertThat(asignacion.evaluacion().estado())
                    .isEqualTo(new EvaluacionAsignadaJuradoReadModel.Estado("EN_PROGRESO", "En progreso"));
            assertThat(asignacion.entregable())
                    .isEqualTo(new EvaluacionAsignadaJuradoReadModel.Entregable(entregable, "Robot seguidor", 2));
        });
    }

    @Test
    void debeFiltrarPorEstadoDelJurado() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var pendiente = sembrarAsignacion(jurado, "Alfa", 1, "PENDIENTE", PENDIENTE_POR_EVALUAR);
        sembrarAsignacion(jurado, "Beta", 1, "EN_PROGRESO", EVALUADA);

        // Act
        var resultado = adapter.consultarTodas(criteria(jurado)
                .raiz(NodoFiltro.predicado("estadoJurado", FiltroOperador.ES, PENDIENTE_POR_EVALUAR))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionAsignadaJuradoReadModel::id)
                .containsExactly(pendiente);
    }

    @Test
    void debeFiltrarPorEstadoDeLaEvaluacion() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        sembrarAsignacion(jurado, "Alfa", 1, "PENDIENTE");
        var finalizada = sembrarAsignacion(jurado, "Beta", 1, "FINALIZADA");

        // Act
        var resultado = adapter.consultarTodas(criteria(jurado)
                .raiz(NodoFiltro.predicado("estado", FiltroOperador.ES, "FINALIZADA"))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionAsignadaJuradoReadModel::id)
                .containsExactly(finalizada);
    }

    @Test
    void debeOrdenarPorEstadoDelJuradoSegunSuOrden_conEvaluadaAlFinal() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var evaluada = sembrarAsignacion(jurado, "Alfa", 1, "PENDIENTE", EVALUADA);
        var pendiente = sembrarAsignacion(jurado, "Beta", 1, "PENDIENTE", PENDIENTE_POR_EVALUAR);

        // Act
        var resultado = adapter.consultarTodas(criteria(jurado)
                .ordenamiento(List.of(SortOrder.of("estadoJurado", SortDirection.ASC)))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionAsignadaJuradoReadModel::id)
                .containsExactly(pendiente, evaluada);
    }

    @Test
    void debeOrdenarPorEstadoProyectoYVersion() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var finalizada = sembrarAsignacion(jurado, "Alfa", 1, "FINALIZADA");
        var betaV2 = sembrarAsignacion(jurado, "Beta", 2, "PENDIENTE");
        var alfaV1 = sembrarAsignacion(jurado, "Alfa", 1, "PENDIENTE");
        var betaV1 = sembrarAsignacion(jurado, "Beta", 1, "PENDIENTE");

        // Act
        var resultado = adapter.consultarTodas(criteria(jurado)
                .ordenamiento(List.of(
                        SortOrder.of("estado", SortDirection.ASC),
                        SortOrder.of("proyecto", SortDirection.ASC),
                        SortOrder.of("version", SortDirection.ASC)))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionAsignadaJuradoReadModel::id)
                .containsExactly(alfaV1, betaV1, betaV2, finalizada);
    }

    @Test
    void debeRetornarVacio_cuandoElJuradoNoTieneAsignaciones() {
        // Arrange
        sembrarAsignacion(UtilUUID.generarNuevoUUID(), "Alfa", 1, "PENDIENTE");

        // Act
        var resultado = adapter.consultarTodas(criteria(UtilUUID.generarNuevoUUID()).build());

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeIndicarSinEvaluacionesRegistradas_cuandoNoHayRegistros() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        sembrarAsignacion(jurado, "Alfa", 1, "PENDIENTE");

        // Act
        var resultado = adapter.consultarTodas(criteria(jurado).build());

        // Assert
        assertThat(resultado.getContent()).singleElement().satisfies(asignacion -> {
            assertThat(asignacion.tieneEvaluacionesCualitativas()).isFalse();
            assertThat(asignacion.tieneEvaluacionesCuantitativas()).isFalse();
        });
    }

    @Test
    void debeIndicarSoloCualitativas_cuandoSoloHayCualitativasRegistradas() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var evaluacionJurado = sembrarAsignacion(jurado, "Alfa", 1, "EN_PROGRESO", PENDIENTE_POR_EVALUAR);
        sembrarEvaluacionCualitativa(evaluacionJurado);
        sembrarEvaluacionCualitativa(evaluacionJurado);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarTodas(criteria(jurado).build());

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent()).singleElement().satisfies(asignacion -> {
            assertThat(asignacion.tieneEvaluacionesCualitativas()).isTrue();
            assertThat(asignacion.tieneEvaluacionesCuantitativas()).isFalse();
            assertThat(asignacion.estado().id()).isEqualTo(PENDIENTE_POR_EVALUAR);
        });
    }

    @Test
    void debeIndicarAmbas_cuandoHayCualitativasYCuantitativasRegistradas() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var evaluacionJurado = sembrarAsignacion(jurado, "Alfa", 1, "EN_PROGRESO", EVALUADA);
        sembrarEvaluacionCualitativa(evaluacionJurado);
        sembrarEvaluacionCuantitativa(evaluacionJurado);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarTodas(criteria(jurado).build());

        // Assert
        assertThat(resultado.getContent()).singleElement().satisfies(asignacion -> {
            assertThat(asignacion.tieneEvaluacionesCualitativas()).isTrue();
            assertThat(asignacion.tieneEvaluacionesCuantitativas()).isTrue();
            assertThat(asignacion.estado().id()).isEqualTo(EVALUADA);
        });
    }
}
