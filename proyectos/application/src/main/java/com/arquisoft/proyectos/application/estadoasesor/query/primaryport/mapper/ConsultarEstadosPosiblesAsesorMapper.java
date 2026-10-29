package com.arquisoft.proyectos.application.estadoasesor.query.primaryport.mapper;

import com.arquisoft.proyectos.application.estadoasesor.query.criteria.EstadoAsesorCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarEstadosPosiblesAsesorMapper {

    private ConsultarEstadosPosiblesAsesorMapper() {}

    public static EstadoAsesorCriteria toCriteria(ConsultaCriteriaQuery query) {
        return EstadoAsesorCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
