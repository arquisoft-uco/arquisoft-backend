package com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoProyectoCriteria;
import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model.ConsultarPlanesEstimadosProyectoGradoAsesorQuery;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public final class ConsultarPlanesEstimadosProyectoGradoAsesorMapper {

    private ConsultarPlanesEstimadosProyectoGradoAsesorMapper() {}

    public static PlanEstimadoProyectoCriteria toCriteria(ConsultarPlanesEstimadosProyectoGradoAsesorQuery query) {
        var criterio = query.criterio();

        return PlanEstimadoProyectoCriteria.builder()
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
                PlanEstimadoProyectoCriteria.Campo.TIEMPO_ESTIMADO_DIAS.getClave(), SortDirection.ASC));
    }
}
