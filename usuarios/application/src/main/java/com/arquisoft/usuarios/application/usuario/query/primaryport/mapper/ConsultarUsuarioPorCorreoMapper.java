package com.arquisoft.usuarios.application.usuario.query.primaryport.mapper;

import com.arquisoft.usuarios.application.usuario.query.criteria.UsuarioPorCorreoCriteria;
import com.arquisoft.usuarios.application.usuario.query.primaryport.model.ConsultarUsuarioPorCorreoQuery;

public final class ConsultarUsuarioPorCorreoMapper {

    private ConsultarUsuarioPorCorreoMapper() {}

    public static UsuarioPorCorreoCriteria toCriteria(ConsultarUsuarioPorCorreoQuery query) {
        return new UsuarioPorCorreoCriteria(query.correo());
    }
}
