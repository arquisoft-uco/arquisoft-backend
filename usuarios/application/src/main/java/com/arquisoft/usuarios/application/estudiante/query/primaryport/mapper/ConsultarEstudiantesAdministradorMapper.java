package com.arquisoft.usuarios.application.estudiante.query.primaryport.mapper;

import com.arquisoft.usuarios.application.estudiante.query.criteria.EstudianteCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarEstudiantesAdministradorMapper {

    private ConsultarEstudiantesAdministradorMapper() {}

    public static EstudianteCriteria toCriteria(ConsultaCriteriaQuery query) {
        return EstudianteCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
