package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarEntregablesProyectoGradoAsesorMapper {

    private ConsultarEntregablesProyectoGradoAsesorMapper() {}

    public static EntregableProyectoGradoCriteria toCriteria(ConsultaCriteriaQuery query) {
        return EntregableProyectoGradoCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
