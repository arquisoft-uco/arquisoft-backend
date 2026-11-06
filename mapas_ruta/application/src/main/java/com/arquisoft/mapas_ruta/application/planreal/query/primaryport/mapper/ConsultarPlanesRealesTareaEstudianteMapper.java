package com.arquisoft.mapas_ruta.application.planreal.query.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealTareaCriteria;
import com.arquisoft.mapas_ruta.application.planreal.query.primaryport.model.ConsultarPlanesRealesTareaEstudianteQuery;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public final class ConsultarPlanesRealesTareaEstudianteMapper {

    private ConsultarPlanesRealesTareaEstudianteMapper() {}

    public static PlanRealTareaCriteria toCriteria(ConsultarPlanesRealesTareaEstudianteQuery query) {
        var criterio = query.criterio();

        return PlanRealTareaCriteria.builder()
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
                PlanRealTareaCriteria.Campo.TIEMPO_REAL_DIAS.getClave(), SortDirection.ASC));
    }
}
