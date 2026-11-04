package com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealTareaCriteria;
import com.arquisoft.mapas_ruta.application.planreal.query.primaryport.mapper.ConsultarPlanesRealesTareaEstudianteMapper;
import com.arquisoft.mapas_ruta.application.planreal.query.primaryport.model.ConsultarPlanesRealesTareaEstudianteQuery;
import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealReadModel;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({PlanRealQueryOutputAdapter.class, PlanRealJpaSpecification.class})
class PlanRealQueryOutputAdapterTest {

    @Autowired
    private PlanRealQueryOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void crearTabla() {
        entityManager.getEntityManager().createNativeQuery(
                "CREATE TABLE IF NOT EXISTS plan_real (id UUID PRIMARY KEY, tarea_id UUID NOT NULL, "
                        + "tiempo_real_dias INTEGER NOT NULL, descripcion_real VARCHAR(200))").executeUpdate();
    }

    private UUID sembrarPlan(UUID tarea, int dias, String descripcion) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.getEntityManager().createNativeQuery(
                        "INSERT INTO plan_real (id, tarea_id, tiempo_real_dias, descripcion_real) VALUES (?, ?, ?, ?)")
                .setParameter(1, id)
                .setParameter(2, tarea)
                .setParameter(3, dias)
                .setParameter(4, descripcion)
                .executeUpdate();
        return id;
    }

    private static PlanRealTareaCriteria criteria(
            UUID tarea, List<SortOrder> orden, NodoFiltro raiz) {
        return ConsultarPlanesRealesTareaEstudianteMapper.toCriteria(
                ConsultarPlanesRealesTareaEstudianteQuery.crear(tarea,
                        ConsultaCriteriaQuery.crear(0, 10, orden, raiz)));
    }

    private static List<Integer> dias(List<PlanRealReadModel> planes) {
        return planes.stream().map(PlanRealReadModel::tiempoRealDias).toList();
    }

    @Test
    void debeDevolverPlanDeLaTarea_cuandoExiste() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var plan = sembrarPlan(tarea, 5, "Revisar capitulo");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorTarea(criteria(tarea, List.of(), null));

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent()).singleElement().satisfies(readModel -> {
            assertThat(readModel.id()).isEqualTo(plan);
            assertThat(readModel.tarea()).isEqualTo(tarea);
            assertThat(readModel.tiempoRealDias()).isEqualTo(5);
            assertThat(readModel.descripcionReal()).isEqualTo("Revisar capitulo");
        });
    }

    @Test
    void debeDevolverVacio_cuandoLaTareaNoTienePlan() {
        // Arrange
        sembrarPlan(UtilUUID.generarNuevoUUID(), 3, "Ajeno");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorTarea(criteria(UtilUUID.generarNuevoUUID(), List.of(), null));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isZero();
    }

    @Test
    void debeNoMezclarPlanesDeOtraTarea_cuandoHayVarias() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        sembrarPlan(tarea, 4, "Propio");
        sembrarPlan(UtilUUID.generarNuevoUUID(), 8, "Ajeno");
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.consultarPorTarea(criteria(tarea, List.of(), null));

        // Assert
        assertThat(resultado.getContent()).extracting(PlanRealReadModel::descripcionReal)
                .containsExactly("Propio");
    }

    @Test
    void debeFiltrarPorTiempo_cuandoOperadorMayorQue() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        sembrarPlan(tarea, 2, "Corto");
        sembrarPlan(tarea, 10, "Largo");
        entityManager.flush();
        entityManager.clear();
        var raiz = NodoFiltro.predicado("tiempoRealDias", FiltroOperador.MAYOR_QUE, "5");

        // Act
        var resultado = adapter.consultarPorTarea(criteria(tarea, List.of(), raiz));

        // Assert
        assertThat(dias(resultado.getContent())).containsExactly(10);
    }

    @Test
    void debeFiltrarPorDescripcion_cuandoContiene() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        sembrarPlan(tarea, 2, "Redactar capitulo");
        sembrarPlan(tarea, 3, "Revisar bibliografia");
        entityManager.flush();
        entityManager.clear();
        var raiz = NodoFiltro.predicado("descripcionReal", FiltroOperador.CONTIENE, "bibliografia");

        // Act
        var resultado = adapter.consultarPorTarea(criteria(tarea, List.of(), raiz));

        // Assert
        assertThat(dias(resultado.getContent())).containsExactly(3);
    }

    @Test
    void debeOrdenarPorTiempo_cuandoSeSolicita() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        sembrarPlan(tarea, 3, "B");
        sembrarPlan(tarea, 1, "A");
        sembrarPlan(tarea, 2, "C");
        entityManager.flush();
        entityManager.clear();
        var orden = List.of(SortOrder.of("tiempoRealDias", SortDirection.DESC));

        // Act
        var resultado = adapter.consultarPorTarea(criteria(tarea, orden, null));

        // Assert
        assertThat(dias(resultado.getContent())).containsExactly(3, 2, 1);
    }
}
