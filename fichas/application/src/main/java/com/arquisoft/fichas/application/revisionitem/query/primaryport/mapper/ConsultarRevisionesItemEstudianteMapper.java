package com.arquisoft.fichas.application.revisionitem.query.primaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemEstudianteCriteria;
import com.arquisoft.fichas.application.revisionitem.query.primaryport.model.ConsultarRevisionesItemEstudianteQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarRevisionesItemEstudianteMapper {

    private ConsultarRevisionesItemEstudianteMapper() {}

    public static RevisionItemEstudianteCriteria toCriteria(ConsultarRevisionesItemEstudianteQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                RevisionItemEstudianteCriteria.Campo.ESTUDIANTE_ID.getClave(), FiltroOperador.ES,
                query.estudiante().toString());

        var raizFinal = UtilObjeto.noEsNulo(criterio.raiz())
                ? NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, criterio.raiz()))
                : forzado;

        return RevisionItemEstudianteCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(criterio.ordenamiento())
                .raiz(raizFinal)
                .build();
    }
}
