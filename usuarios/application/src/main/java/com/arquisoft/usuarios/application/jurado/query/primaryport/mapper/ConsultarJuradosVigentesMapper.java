package com.arquisoft.usuarios.application.jurado.query.primaryport.mapper;

import com.arquisoft.usuarios.application.jurado.query.criteria.JuradoVigenteCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarJuradosVigentesMapper {

    private ConsultarJuradosVigentesMapper() {}

    public static JuradoVigenteCriteria toCriteria(ConsultaCriteriaQuery query) {
        return JuradoVigenteCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
