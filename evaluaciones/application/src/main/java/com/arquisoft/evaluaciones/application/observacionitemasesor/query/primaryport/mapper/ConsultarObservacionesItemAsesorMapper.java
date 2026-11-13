package com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.criteria.ObservacionItemAsesorCriteria;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.model.ConsultarObservacionesItemAsesorQuery;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public final class ConsultarObservacionesItemAsesorMapper {

    private ConsultarObservacionesItemAsesorMapper() {}

    public static ObservacionItemAsesorCriteria toCriteria(ConsultarObservacionesItemAsesorQuery query) {
        var criterio = query.criterio();

        var ordenamiento = UtilColeccion.esVaciaONula(criterio.ordenamiento())
                ? List.of(SortOrder.of(ObservacionItemAsesorCriteria.Campo.DESCRIPCION.getClave(), SortDirection.ASC))
                : criterio.ordenamiento();

        return ObservacionItemAsesorCriteria.builder()
                .evaluacionCuantitativaAsesor(query.evaluacionCuantitativaAsesor())
                .asesor(query.asesor())
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento)
                .raiz(criterio.raiz())
                .build();
    }
}
