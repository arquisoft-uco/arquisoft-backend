package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionesAsignadasAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsignadaAsesorReadModel;
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

// evaluacion_asesor y evaluacion tienen @Entity de comando (Hibernate las crea); entregable, estado_evaluacion y las
// tablas de cualitativas/cuantitativas no, asi que se crean a mano.
// @DirtiesContext: el CREATE TABLE nativo hace commit implicito en H2 y rompe el rollback
// transaccional de @DataJpaTest entre clases que comparten el mismo contexto cacheado.
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EvaluacionAsignadaAsesorQueryOutputAdapterTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EvaluacionAsignadaAsesorQueryRepository repository;

    private EvaluacionAsignadaAsesorQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionAsignadaAsesorQueryOutputAdapter(
                repository, new EvaluacionAsignadaAsesorJpaSpecification());
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
                CREATE TABLE IF NOT EXISTS evaluacion_cualitativa_asesor (
                    id UUID NOT NULL, evaluacion_asesor_id UUID NOT NULL,
                    item_id UUID NOT NULL, criterio_id UUID NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
        entityManager.createNativeQuery("""
                CREATE TABLE IF NOT EXISTS evaluacion_cuantitativa_asesor (
                    id UUID NOT NULL, evaluacion_asesor_id UUID NOT NULL,
                    item_id UUID NOT NULL, puntaje INTEGER NOT NULL, PRIMARY KEY (id))
                """).executeUpdate();
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

    private UUID sembrarAsignacion(UUID asesor, UUID entregable, String proyecto, int version, String estado) {
        entityManager.createNativeQuery(
                        "INSERT INTO entregable (id, proyecto, version_entregable, proyecto_grado_id) VALUES (?, ?, ?, ?)")
                .setParameter(1, entregable)
                .setParameter(2, proyecto)
                .setParameter(3, version)
                .setParameter(4, UtilUUID.generarNuevoUUID())
                .executeUpdate();
        var evaluacion = UtilUUID.generarNuevoUUID();
        entityManager.createNativeQuery(
                        "INSERT INTO evaluacion (id, entregable_id, estado_evaluacion_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacion)
                .setParameter(2, entregable)
                .setParameter(3, estado)
                .executeUpdate();
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        entityManager.createNativeQuery(
                        "INSERT INTO evaluacion_asesor (id, evaluacion_id, asesor_id) VALUES (?, ?, ?)")
                .setParameter(1, evaluacionAsesor)
                .setParameter(2, evaluacion)
                .setParameter(3, asesor)
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
        return evaluacionAsesor;
    }

    private UUID sembrarAsignacion(UUID asesor, String proyecto, int version, String estado) {
        return sembrarAsignacion(asesor, UtilUUID.generarNuevoUUID(), proyecto, version, estado);
    }

    private void sembrarEvaluacionCualitativa(UUID evaluacionAsesor) {
        entityManager.createNativeQuery("""
                        INSERT INTO evaluacion_cualitativa_asesor (id, evaluacion_asesor_id, item_id, criterio_id)
                        VALUES (?, ?, ?, ?)
                        """)
                .setParameter(1, UtilUUID.generarNuevoUUID())
                .setParameter(2, evaluacionAsesor)
                .setParameter(3, UtilUUID.generarNuevoUUID())
                .setParameter(4, UtilUUID.generarNuevoUUID())
                .executeUpdate();
    }

    private void sembrarEvaluacionCuantitativa(UUID evaluacionAsesor) {
        entityManager.createNativeQuery("""
                        INSERT INTO evaluacion_cuantitativa_asesor (id, evaluacion_asesor_id, item_id, puntaje)
                        VALUES (?, ?, ?, ?)
                        """)
                .setParameter(1, UtilUUID.generarNuevoUUID())
                .setParameter(2, evaluacionAsesor)
                .setParameter(3, UtilUUID.generarNuevoUUID())
                .setParameter(4, 4)
                .executeUpdate();
    }

    private static EvaluacionesAsignadasAsesorCriteria.Builder criteria(UUID asesor) {
        return EvaluacionesAsignadasAsesorCriteria.builder().asesor(asesor);
    }

    @Test
    void debeDevolverSoloEvaluacionesDelAsesor() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var propia = sembrarAsignacion(asesor, "Alfa", 1, "PENDIENTE");
        sembrarAsignacion(UtilUUID.generarNuevoUUID(), "Beta", 1, "PENDIENTE");

        // Act
        var resultado = adapter.consultarTodas(criteria(asesor).build());

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent()).extracting(EvaluacionAsignadaAsesorReadModel::id).containsExactly(propia);
    }

    @Test
    void debeMapearIdEvaluacionEstadoYEntregable() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = sembrarAsignacion(asesor, entregable, "Robot seguidor", 2, "EN_PROGRESO");

        // Act
        var resultado = adapter.consultarTodas(criteria(asesor).build());

        // Assert
        assertThat(resultado.getContent()).singleElement().satisfies(asignacion -> {
            assertThat(asignacion.id()).isEqualTo(evaluacionAsesor);
            assertThat(asignacion.evaluacion().id()).isNotNull().isNotEqualTo(evaluacionAsesor);
            assertThat(asignacion.evaluacion().estado())
                    .isEqualTo(new EvaluacionAsignadaAsesorReadModel.Estado("EN_PROGRESO", "En progreso"));
            assertThat(asignacion.entregable())
                    .isEqualTo(new EvaluacionAsignadaAsesorReadModel.Entregable(entregable, "Robot seguidor", 2));
        });
    }

    @Test
    void debeFiltrarPorEstado() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        sembrarAsignacion(asesor, "Alfa", 1, "PENDIENTE");
        var finalizada = sembrarAsignacion(asesor, "Beta", 1, "FINALIZADA");

        // Act
        var resultado = adapter.consultarTodas(criteria(asesor)
                .raiz(NodoFiltro.predicado("estado", FiltroOperador.ES, "FINALIZADA"))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionAsignadaAsesorReadModel::id)
                .containsExactly(finalizada);
    }

    @Test
    void debeFiltrarPorProyecto() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        sembrarAsignacion(asesor, "Alfa", 1, "PENDIENTE");
        var beta = sembrarAsignacion(asesor, "Beta", 1, "PENDIENTE");

        // Act
        var resultado = adapter.consultarTodas(criteria(asesor)
                .raiz(NodoFiltro.predicado("proyecto", FiltroOperador.ES, "Beta"))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionAsignadaAsesorReadModel::id)
                .containsExactly(beta);
    }

    @Test
    void debeOrdenarPorEstadoProyectoYVersion() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var finalizada = sembrarAsignacion(asesor, "Alfa", 1, "FINALIZADA");
        var betaV2 = sembrarAsignacion(asesor, "Beta", 2, "PENDIENTE");
        var alfaV1 = sembrarAsignacion(asesor, "Alfa", 1, "PENDIENTE");
        var betaV1 = sembrarAsignacion(asesor, "Beta", 1, "PENDIENTE");

        // Act
        var resultado = adapter.consultarTodas(criteria(asesor)
                .ordenamiento(List.of(
                        SortOrder.of("estado", SortDirection.ASC),
                        SortOrder.of("proyecto", SortDirection.ASC),
                        SortOrder.of("version", SortDirection.ASC)))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionAsignadaAsesorReadModel::id)
                .containsExactly(alfaV1, betaV1, betaV2, finalizada);
    }

    @Test
    void debeOrdenarPorVersionDescendente() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var v1 = sembrarAsignacion(asesor, "Alfa", 1, "PENDIENTE");
        var v3 = sembrarAsignacion(asesor, "Alfa", 3, "PENDIENTE");
        var v2 = sembrarAsignacion(asesor, "Alfa", 2, "PENDIENTE");

        // Act
        var resultado = adapter.consultarTodas(criteria(asesor)
                .ordenamiento(List.of(SortOrder.of("version", SortDirection.DESC)))
                .build());

        // Assert
        assertThat(resultado.getContent())
                .extracting(EvaluacionAsignadaAsesorReadModel::id)
                .containsExactly(v3, v2, v1);
    }

    @Test
    void debeRetornarVacio_cuandoElAsesorNoTieneEvaluaciones() {
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
        var asesor = UtilUUID.generarNuevoUUID();
        sembrarAsignacion(asesor, "Alfa", 1, "PENDIENTE");

        // Act
        var resultado = adapter.consultarTodas(criteria(asesor).build());

        // Assert
        assertThat(resultado.getContent()).singleElement().satisfies(asignacion -> {
            assertThat(asignacion.tieneEvaluacionesCualitativas()).isFalse();
            assertThat(asignacion.tieneEvaluacionesCuantitativas()).isFalse();
        });
    }

    @Test
    void debeIndicarSoloCualitativas_cuandoSoloHayCualitativas() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = sembrarAsignacion(asesor, "Alfa", 1, "PENDIENTE");
        sembrarEvaluacionCualitativa(evaluacionAsesor);
        sembrarEvaluacionCualitativa(evaluacionAsesor);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarTodas(criteria(asesor).build());

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent()).singleElement().satisfies(asignacion -> {
            assertThat(asignacion.tieneEvaluacionesCualitativas()).isTrue();
            assertThat(asignacion.tieneEvaluacionesCuantitativas()).isFalse();
        });
    }

    @Test
    void debeIndicarSoloCuantitativas_cuandoSoloHayCuantitativas() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = sembrarAsignacion(asesor, "Alfa", 1, "PENDIENTE");
        sembrarEvaluacionCuantitativa(evaluacionAsesor);
        sembrarEvaluacionCuantitativa(evaluacionAsesor);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarTodas(criteria(asesor).build());

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent()).singleElement().satisfies(asignacion -> {
            assertThat(asignacion.tieneEvaluacionesCualitativas()).isFalse();
            assertThat(asignacion.tieneEvaluacionesCuantitativas()).isTrue();
        });
    }

    @Test
    void debeIndicarAmbas_cuandoHayAmbasRegistradas() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var evaluacionAsesor = sembrarAsignacion(asesor, "Alfa", 1, "PENDIENTE");
        sembrarEvaluacionCualitativa(evaluacionAsesor);
        sembrarEvaluacionCuantitativa(evaluacionAsesor);
        entityManager.flush();

        // Act
        var resultado = adapter.consultarTodas(criteria(asesor).build());

        // Assert
        assertThat(resultado.getContent()).singleElement().satisfies(asignacion -> {
            assertThat(asignacion.tieneEvaluacionesCualitativas()).isTrue();
            assertThat(asignacion.tieneEvaluacionesCuantitativas()).isTrue();
        });
    }
}
