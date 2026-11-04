package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoCoordinadorQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;

public final class ConsultarProyectosGradoCoordinadorMapper {

    private ConsultarProyectosGradoCoordinadorMapper() {}

    public static ProyectoGradoCriteria toCriteria(ConsultarProyectosGradoCoordinadorQuery query) {
        var criterio = query.criterio();

        var forzado = NodoFiltro.predicado(
                ProyectoGradoCriteria.Campo.COORDINADOR.getClave(), FiltroOperador.ES,
                query.coordinador().toString());

        var raizFinal = UtilObjeto.noEsNulo(criterio.raiz())
                ? NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, criterio.raiz()))
                : forzado;

        return ProyectoGradoCriteria.builder()
                .pagina(criterio.pagina())
                .tamanio(criterio.tamanio())
                .ordenamiento(criterio.ordenamiento())
                .raiz(raizFinal)
                .build();
    }
}
