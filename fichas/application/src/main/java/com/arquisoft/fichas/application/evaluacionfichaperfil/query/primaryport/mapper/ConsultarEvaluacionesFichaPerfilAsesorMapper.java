package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilAsesorQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarEvaluacionesFichaPerfilAsesorMapper {

    private ConsultarEvaluacionesFichaPerfilAsesorMapper() {}

    public static EvaluacionFichaPerfilAsesorCriteria toCriteria(ConsultarEvaluacionesFichaPerfilAsesorQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                EvaluacionFichaPerfilAsesorCriteria.Campo.ASESOR_FICHA.getClave(), FiltroOperador.ES,
                query.asesorFicha().toString());

        var raizFinal = UtilObjeto.noEsNulo(criterio.raiz())
                ? NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, criterio.raiz()))
                : forzado;

        return EvaluacionFichaPerfilAsesorCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(criterio.ordenamiento())
                .raiz(raizFinal)
                .build();
    }
}
