package com.arquisoft.mapas_ruta.application.tarea.query.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.tarea.query.criteria.TareaMapaRutaCriteria;
import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarTareasMapaRutaEstudianteQuery;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public final class ConsultarTareasMapaRutaEstudianteMapper {

    private ConsultarTareasMapaRutaEstudianteMapper() {}

    public static TareaMapaRutaCriteria toCriteria(ConsultarTareasMapaRutaEstudianteQuery query) {
        var criterio = query.criterio();

        return TareaMapaRutaCriteria.builder()
                .mapaRuta(query.mapaRuta())
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
        return List.of(SortOrder.of(TareaMapaRutaCriteria.Campo.DESCRIPCION.getClave(), SortDirection.ASC));
    }
}
