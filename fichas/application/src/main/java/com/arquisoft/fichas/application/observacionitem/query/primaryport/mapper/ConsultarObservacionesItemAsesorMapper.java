package com.arquisoft.fichas.application.observacionitem.query.primaryport.mapper;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemCriteria;
import com.arquisoft.fichas.application.observacionitem.query.primaryport.model.ConsultarObservacionesItemAsesorQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarObservacionesItemAsesorMapper {

    private ConsultarObservacionesItemAsesorMapper() {}

    public static ObservacionItemCriteria toCriteria(ConsultarObservacionesItemAsesorQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                ObservacionItemCriteria.Campo.ASESOR_ID.getClave(), FiltroOperador.ES,
                query.asesorFicha().toString());

        var raizFinal = UtilObjeto.noEsNulo(criterio.raiz())
                ? NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, criterio.raiz()))
                : forzado;

        return ObservacionItemCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(criterio.ordenamiento())
                .raiz(raizFinal)
                .build();
    }
}
