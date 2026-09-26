package com.arquisoft.usuarios.application.asesorficha.command.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record RemoverAsesorFichaCommand(UUID usuario) {

    public static RemoverAsesorFichaCommand crear(UUID usuario) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(usuario, UsuariosFields.AsesorFicha.USUARIO,
                UsuariosCodes.AsesorFicha.USUARIO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverAsesorFichaCommand(usuario);
    }
}
