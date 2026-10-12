package com.arquisoft.usuarios.application.estudiante.query.primaryport.mapper;

import com.arquisoft.usuarios.application.estudiante.query.criteria.EstudianteVigenteCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarEstudiantesVigentesMapper {

    private ConsultarEstudiantesVigentesMapper() {}

    public static EstudianteVigenteCriteria toCriteria(ConsultaCriteriaQuery query) {
        return EstudianteVigenteCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
