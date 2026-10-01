package com.arquisoft.fichas.application.fichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilAsesoradasQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarFichasPerfilAsesoradasMapper {

    private ConsultarFichasPerfilAsesoradasMapper() {}

    public static FichaPerfilCriteria toCriteria(ConsultarFichasPerfilAsesoradasQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                FichaPerfilCriteria.Campo.ASESOR_ID.getClave(), FiltroOperador.ES,
                query.asesorFicha().toString());

        var raizFinal = UtilObjeto.noEsNulo(criterio.raiz())
                ? NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, criterio.raiz()))
                : forzado;

        return FichaPerfilCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(criterio.ordenamiento())
                .raiz(raizFinal)
                .build();
    }
}
