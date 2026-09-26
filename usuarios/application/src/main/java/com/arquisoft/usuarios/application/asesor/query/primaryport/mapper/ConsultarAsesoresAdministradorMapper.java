package com.arquisoft.usuarios.application.asesor.query.primaryport.mapper;

import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarAsesoresAdministradorMapper {

    private ConsultarAsesoresAdministradorMapper() {}

    public static AsesorCriteria toCriteria(ConsultaCriteriaQuery query) {
        return AsesorCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
