package com.arquisoft.usuarios.application.usuario.command.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record CambiarEstadoUsuarioCommand(UUID usuario, String estado) {

    public static CambiarEstadoUsuarioCommand crear(UUID usuario, String estado) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(usuario, UsuariosFields.Usuario.USUARIO,
                UsuariosCodes.Usuario.USUARIO_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(estado, UsuariosFields.Usuario.ESTADO,
                UsuariosCodes.Usuario.ESTADO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarEstadoUsuarioCommand(usuario, estado);
    }
}
