package com.arquisoft.usuarios.application.administrador.query.primaryport.mapper;

import com.arquisoft.usuarios.application.administrador.query.criteria.AdministradorCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarAdministradoresAdministradorMapper {

    private ConsultarAdministradoresAdministradorMapper() {}

    public static AdministradorCriteria toCriteria(ConsultaCriteriaQuery query) {
        return AdministradorCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
