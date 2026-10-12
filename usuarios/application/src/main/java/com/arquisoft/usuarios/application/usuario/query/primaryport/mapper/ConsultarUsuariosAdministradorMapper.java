package com.arquisoft.usuarios.application.usuario.query.primaryport.mapper;

import com.arquisoft.usuarios.application.usuario.query.criteria.UsuarioCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarUsuariosAdministradorMapper {

    private ConsultarUsuariosAdministradorMapper() {}

    public static UsuarioCriteria toCriteria(ConsultaCriteriaQuery query) {
        return UsuarioCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
