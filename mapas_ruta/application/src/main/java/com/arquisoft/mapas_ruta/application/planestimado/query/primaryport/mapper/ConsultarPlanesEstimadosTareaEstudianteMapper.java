package com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoTareaCriteria;
import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model.ConsultarPlanesEstimadosTareaEstudianteQuery;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public final class ConsultarPlanesEstimadosTareaEstudianteMapper {

    private ConsultarPlanesEstimadosTareaEstudianteMapper() {}

    public static PlanEstimadoTareaCriteria toCriteria(ConsultarPlanesEstimadosTareaEstudianteQuery query) {
        var criterio = query.criterio();

        return PlanEstimadoTareaCriteria.builder()
                .tarea(query.tarea())
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento(criterio.ordenamiento()))
                .raiz(criterio.raiz())
                .build();
    }

    private static List<SortOrder> ordenamiento(List<SortOrder> solicitado) {
        if (!UtilColeccion.esVaciaONula(solicitado)) {
            return solicitado;
        }
        return List.of(SortOrder.of(
                PlanEstimadoTareaCriteria.Campo.TIEMPO_ESTIMADO_DIAS.getClave(), SortDirection.ASC));
    }
}
