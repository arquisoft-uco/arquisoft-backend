package com.arquisoft.usuarios.application.usuario.query.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarIdentidadUsuarioQuery(UUID usuario) {

    public static ConsultarIdentidadUsuarioQuery crear(UUID usuario) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(usuario, UsuariosFields.Usuario.USUARIO,
                UsuariosCodes.Usuario.USUARIO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarIdentidadUsuarioQuery(usuario);
    }
}
