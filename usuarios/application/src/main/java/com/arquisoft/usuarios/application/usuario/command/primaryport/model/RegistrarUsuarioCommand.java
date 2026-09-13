package com.arquisoft.usuarios.application.usuario.command.primaryport.model;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.message.key.usuarios.RegistrarUsuarioKey;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.List;

public record RegistrarUsuarioCommand(
        String identificador,
        String nombres,
        String apellidos,
        String email,
        String contacto,
        List<String> roles
) {
    public RegistrarUsuarioCommand {
        identificador = UtilTexto.aplicarTrim(identificador);
        nombres = UtilTexto.aplicarTrim(nombres);
        apellidos = UtilTexto.aplicarTrim(apellidos);
        email = UtilTexto.aplicarTrim(email);
        contacto = UtilTexto.aplicarTrim(contacto);
        roles = UtilColeccion.aplicarPorDefecto(roles);
    }

    public static RegistrarUsuarioCommand crear(
            String identificador, String nombres, String apellidos, String email,
            String contacto, List<String> roles) {
        var result = new ValidationResult();

        ValidatorTexto.noEnBlanco(identificador, UsuariosFields.Usuario.IDENTIFICADOR,
                UsuariosCodes.Usuario.IDENTIFICADOR_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombres, UsuariosFields.Usuario.NOMBRES,
                UsuariosCodes.Usuario.NOMBRES_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(apellidos, UsuariosFields.Usuario.APELLIDOS,
                UsuariosCodes.Usuario.APELLIDOS_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(email, UsuariosFields.Usuario.EMAIL,
                UsuariosCodes.Usuario.EMAIL_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(contacto, UsuariosFields.Usuario.CONTACTO,
                UsuariosCodes.Usuario.CONTACTO_REQUERIDO, result);

        var rolesNormalizados = UtilColeccion.aplicarPorDefecto(roles).stream()
                .map(UtilTexto::aplicarTrim)
                .toList();
        rolesNormalizados.stream()
                .filter(UtilTexto::esVacioONulo)
                .forEach(rol -> result.agregarError(UsuariosFields.Usuario.ROLES,
                        UsuariosCodes.Usuario.ROL_REQUERIDO,
                        Mensajes.obtener(RegistrarUsuarioKey.ERROR_ROL_REQUERIDO)));
        rolesNormalizados.stream()
                .filter(rol -> !UtilTexto.esVacioONulo(rol))
                .filter(rol -> !UsuariosRealmRoles.CONOCIDOS.contains(rol))
                .forEach(rol -> result.agregarError(UsuariosFields.Usuario.ROLES,
                        UsuariosCodes.Usuario.ROL_NO_VALIDO,
                        Mensajes.formatear(RegistrarUsuarioKey.ERROR_ROL_NO_VALIDO, rol)));

        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarUsuarioCommand(
                identificador, nombres, apellidos, email, contacto, rolesNormalizados);
    }
}
