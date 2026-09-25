package com.arquisoft.usuarios.application.asesorficha.query.primaryport.mapper;

import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarAsesoresFichaAdministradorMapper {

    private ConsultarAsesoresFichaAdministradorMapper() {}

    public static AsesorFichaCriteria toCriteria(ConsultaCriteriaQuery query) {
        return AsesorFichaCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
