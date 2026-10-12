package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilAsesorQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarEstadosFichaPerfilAsesorMapper {

    private ConsultarEstadosFichaPerfilAsesorMapper() {}

    public static EstadoFichaPerfilAsesorCriteria toCriteria(ConsultarEstadosFichaPerfilAsesorQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                EstadoFichaPerfilAsesorCriteria.Campo.ASESOR_FICHA.getClave(), FiltroOperador.ES,
                query.asesorFicha().toString());

        var raizFinal = UtilObjeto.noEsNulo(criterio.raiz())
                ? NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, criterio.raiz()))
                : forzado;

        return EstadoFichaPerfilAsesorCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(criterio.ordenamiento())
                .raiz(raizFinal)
                .build();
    }
}
