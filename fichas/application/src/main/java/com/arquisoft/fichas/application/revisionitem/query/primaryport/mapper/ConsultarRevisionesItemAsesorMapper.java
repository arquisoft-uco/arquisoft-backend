package com.arquisoft.fichas.application.revisionitem.query.primaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemCriteria;
import com.arquisoft.fichas.application.revisionitem.query.primaryport.model.ConsultarRevisionesItemAsesorQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarRevisionesItemAsesorMapper {

    private ConsultarRevisionesItemAsesorMapper() {}

    public static RevisionItemCriteria toCriteria(ConsultarRevisionesItemAsesorQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                RevisionItemCriteria.Campo.ASESOR_ID.getClave(), FiltroOperador.ES,
                query.asesorFicha().toString());

        var raizFinal = UtilObjeto.noEsNulo(criterio.raiz())
                ? NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, criterio.raiz()))
                : forzado;

        return RevisionItemCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(criterio.ordenamiento())
                .raiz(raizFinal)
                .build();
    }
}
