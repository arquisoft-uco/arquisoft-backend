package com.arquisoft.usuarios.application.representantecomitecurriculum.command.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record AgregarRepresentanteComiteCurriculumCommand(
        UUID usuario
) {

    public AgregarRepresentanteComiteCurriculumCommand {
    }

    public static AgregarRepresentanteComiteCurriculumCommand crear(String usuario) {
        var resultado = new ValidationResult();

        ValidatorTexto.noEnBlanco(
                usuario,
                UsuariosFields.RepresentanteComiteCurriculum.USUARIO,
                UsuariosCodes.RepresentanteComiteCurriculum.USUARIO_REQUERIDO,
                resultado
        );

        ValidatorUUID.uuidValido(
                usuario,
                UsuariosFields.RepresentanteComiteCurriculum.USUARIO,
                UsuariosCodes.RepresentanteComiteCurriculum.USUARIO_FORMATO_INVALIDO,
                resultado
        );

        resultado.lanzarSiTieneErrores();

        return new AgregarRepresentanteComiteCurriculumCommand(
                UtilUUID.generarUUIDDesdeTexto(usuario)
        );
    }
}
