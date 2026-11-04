package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionEstudianteCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.model.ConsultarEvaluacionesEstudianteQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarEvaluacionesEstudianteMapper {

    private ConsultarEvaluacionesEstudianteMapper() {}

    public static EvaluacionEstudianteCriteria toCriteria(ConsultarEvaluacionesEstudianteQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                EvaluacionEstudianteCriteria.Campo.ESTUDIANTE_ID.getClave(), FiltroOperador.ES,
                query.estudiante().toString());

        var raizFinal = UtilObjeto.noEsNulo(criterio.raiz())
                ? NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, criterio.raiz()))
                : forzado;

        var ordenamiento = UtilColeccion.esVaciaONula(criterio.ordenamiento())
                ? List.of(SortOrder.of(EvaluacionEstudianteCriteria.Campo.PROYECTO.getClave(), SortDirection.ASC))
                : criterio.ordenamiento();

        return EvaluacionEstudianteCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento)
                .raiz(raizFinal)
                .build();
    }
}
