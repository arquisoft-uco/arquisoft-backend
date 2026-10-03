package com.arquisoft.usuarios.application.asesorficha.query.primaryport.mapper;

import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaVigenteCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarAsesoresFichaVigentesMapper {

    private ConsultarAsesoresFichaVigentesMapper() {}

    public static AsesorFichaVigenteCriteria toCriteria(ConsultaCriteriaQuery query) {
        return AsesorFichaVigenteCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
