package com.arquisoft.mapas_ruta.application.planreal.query.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealProyectoCriteria;
import com.arquisoft.mapas_ruta.application.planreal.query.primaryport.model.ConsultarPlanesRealesProyectoGradoAsesorQuery;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public final class ConsultarPlanesRealesProyectoGradoAsesorMapper {

    private ConsultarPlanesRealesProyectoGradoAsesorMapper() {}

    public static PlanRealProyectoCriteria toCriteria(ConsultarPlanesRealesProyectoGradoAsesorQuery query) {
        var criterio = query.criterio();

        return PlanRealProyectoCriteria.builder()
                .proyectoGrado(query.proyectoGrado())
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
                PlanRealProyectoCriteria.Campo.TIEMPO_REAL_DIAS.getClave(), SortDirection.ASC));
    }
}
