package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaCriteria;
import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapasRutaCoordinadorQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarMapasRutaCoordinadorMapper {

    private ConsultarMapasRutaCoordinadorMapper() {}

    public static MapaRutaCriteria toCriteria(ConsultarMapasRutaCoordinadorQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                MapaRutaCriteria.Campo.COORDINADOR.getClave(), FiltroOperador.ES,
                query.coordinador().toString());

        var raizFinal = UtilObjeto.noEsNulo(criterio.raiz())
                ? NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, criterio.raiz()))
                : forzado;

        return MapaRutaCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(ordenamiento(criterio.ordenamiento()))
                .raiz(raizFinal)
                .build();
    }

    private static List<SortOrder> ordenamiento(List<SortOrder> solicitado) {
        if (!UtilColeccion.esVaciaONula(solicitado)) {
            return solicitado;
        }
        return List.of(
                SortOrder.of(MapaRutaCriteria.Campo.FECHA_INICIO.getClave(), SortDirection.DESC),
                SortOrder.of(MapaRutaCriteria.Campo.FECHA_FIN.getClave(), SortDirection.ASC));
    }
}
