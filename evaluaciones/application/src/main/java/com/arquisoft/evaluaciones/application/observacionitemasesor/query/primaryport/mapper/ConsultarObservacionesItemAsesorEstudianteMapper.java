package com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.criteria.ObservacionItemAsesorCriteria;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.model.ConsultarObservacionesItemAsesorEstudianteQuery;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public final class ConsultarObservacionesItemAsesorEstudianteMapper {

    private ConsultarObservacionesItemAsesorEstudianteMapper() {}

    public static ObservacionItemAsesorCriteria toCriteria(ConsultarObservacionesItemAsesorEstudianteQuery query) {
        var criterio = query.criterio();

        var ordenamiento = UtilColeccion.esVaciaONula(criterio.ordenamiento())
                ? List.of(SortOrder.of(ObservacionItemAsesorCriteria.Campo.DESCRIPCION.getClave(), SortDirection.ASC))
                : criterio.ordenamiento();

        return ObservacionItemAsesorCriteria.builder()
                .evaluacionCuantitativaAsesor(query.evaluacionCuantitativaAsesor())
                .estudiante(query.estudiante())
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento)
                .raiz(criterio.raiz())
                .build();
    }
}
