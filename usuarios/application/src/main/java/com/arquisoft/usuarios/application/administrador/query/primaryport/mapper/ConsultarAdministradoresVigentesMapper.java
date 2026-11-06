package com.arquisoft.usuarios.application.administrador.query.primaryport.mapper;

import com.arquisoft.usuarios.application.administrador.query.criteria.AdministradorVigenteCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarAdministradoresVigentesMapper {

    private ConsultarAdministradoresVigentesMapper() {}

    public static AdministradorVigenteCriteria toCriteria(ConsultaCriteriaQuery query) {
        return AdministradorVigenteCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
