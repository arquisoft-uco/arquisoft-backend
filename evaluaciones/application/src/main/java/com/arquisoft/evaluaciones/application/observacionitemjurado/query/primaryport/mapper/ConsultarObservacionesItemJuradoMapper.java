package com.arquisoft.evaluaciones.application.observacionitemjurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.query.criteria.ObservacionItemJuradoCriteria;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.primaryport.model.ConsultarObservacionesItemJuradoQuery;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public final class ConsultarObservacionesItemJuradoMapper {

    private ConsultarObservacionesItemJuradoMapper() {}

    public static ObservacionItemJuradoCriteria toCriteria(ConsultarObservacionesItemJuradoQuery query) {
        var criterio = query.criterio();

        var ordenamiento = UtilColeccion.esVaciaONula(criterio.ordenamiento())
                ? List.of(SortOrder.of(ObservacionItemJuradoCriteria.Campo.DESCRIPCION.getClave(), SortDirection.ASC))
                : criterio.ordenamiento();

        return ObservacionItemJuradoCriteria.builder()
                .evaluacionCuantitativaJurado(query.evaluacionCuantitativaJurado())
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento)
                .raiz(criterio.raiz())
                .build();
    }
}
