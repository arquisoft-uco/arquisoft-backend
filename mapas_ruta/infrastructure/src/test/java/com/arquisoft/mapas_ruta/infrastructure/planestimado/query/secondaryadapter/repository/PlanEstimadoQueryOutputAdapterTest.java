package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoTareaCriteria;
import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.mapper.ConsultarPlanesEstimadosTareaEstudianteMapper;
import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model.ConsultarPlanesEstimadosTareaEstudianteQuery;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoReadModel;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.command.secondaryadapter.entity.PlanEstimadoJpaEntity;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({PlanEstimadoQueryOutputAdapter.class, PlanEstimadoJpaSpecification.class})
class PlanEstimadoQueryOutputAdapterTest {

    @Autowired
    private PlanEstimadoQueryOutputAdapter adapter;

    @Autowired
    private TestEntityManager entityManager;

    private UUID sembrarPlan(UUID tarea, int dias, String descripcion) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(PlanEstimadoJpaEntity.builder()
                .id(id)
                .tareaId(tarea)
                .tiempoEstimadoDias(dias)
                .descripcionEstimado(descripcion)
                .build());
        return id;
    }

    private static PlanEstimadoTareaCriteria criteria(
            UUID tarea, List<SortOrder> orden, NodoFiltro raiz) {
        return ConsultarPlanesEstimadosTareaEstudianteMapper.toCriteria(
                ConsultarPlanesEstimadosTareaEstudianteQuery.crear(tarea,
                        ConsultaCriteriaQuery.crear(0, 10, orden, raiz)));
    }

    private static List<Integer> dias(List<PlanEstimadoReadModel> planes) {
        return planes.stream().map(PlanEstimadoReadModel::tiempoEstimadoDias).toList();
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
            assertThat(readModel.tiempoEstimadoDias()).isEqualTo(5);
            assertThat(readModel.descripcionEstimado()).isEqualTo("Revisar capitulo");
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
        assertThat(resultado.getContent()).extracting(PlanEstimadoReadModel::descripcionEstimado)
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
        var raiz = NodoFiltro.predicado("tiempoEstimadoDias", FiltroOperador.MAYOR_QUE, "5");

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
        var raiz = NodoFiltro.predicado("descripcionEstimado", FiltroOperador.CONTIENE, "bibliografia");

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
        var orden = List.of(SortOrder.of("tiempoEstimadoDias", SortDirection.DESC));

        // Act
        var resultado = adapter.consultarPorTarea(criteria(tarea, orden, null));

        // Assert
        assertThat(dias(resultado.getContent())).containsExactly(3, 2, 1);
    }
}
