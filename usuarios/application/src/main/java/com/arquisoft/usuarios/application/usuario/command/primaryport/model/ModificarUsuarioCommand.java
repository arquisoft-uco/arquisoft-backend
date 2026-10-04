package com.arquisoft.usuarios.application.usuario.command.primaryport.model;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.message.key.usuarios.ModificarUsuarioKey;
import com.arquisoft.shared.message.key.usuarios.RegistrarUsuarioKey;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public record ModificarUsuarioCommand(
        UUID usuario,
        String identificador,
        String nombre,
        String email,
        String contacto,
        String nombres,
        String apellidos,
        List<String> roles
) {
    public record DatosModificarUsuario(String identificador, String nombre, String email,
                                        String contacto, String nombres, String apellidos) {}

    public ModificarUsuarioCommand {
        identificador = recortarSiLlega(identificador);
        nombre = recortarSiLlega(nombre);
        email = recortarSiLlega(email);
        contacto = recortarSiLlega(contacto);
        nombres = recortarSiLlega(nombres);
        apellidos = recortarSiLlega(apellidos);
        roles = UtilColeccion.aplicarPorDefecto(roles);
    }

    public static ModificarUsuarioCommand crear(String usuario, DatosModificarUsuario datos,
                                                List<String> roles) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(usuario, UsuariosFields.Usuario.USUARIO,
                UsuariosCodes.Usuario.USUARIO_FORMATO, result);

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

        if (sinDatos(datos) && rolesNormalizados.isEmpty()) {
            result.agregarError(UsuariosFields.Usuario.USUARIO,
                    UsuariosCodes.Usuario.MODIFICACION_VACIA,
                    Mensajes.obtener(ModificarUsuarioKey.ERROR_MODIFICACION_VACIA));
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new ModificarUsuarioCommand(
                UtilUUID.generarUUIDDesdeTexto(usuario), datos.identificador(), datos.nombre(),
                datos.email(), datos.contacto(), datos.nombres(), datos.apellidos(), rolesNormalizados);
    }

    private static boolean sinDatos(DatosModificarUsuario datos) {
        return Stream.of(datos.identificador(), datos.nombre(), datos.email(),
                        datos.contacto(), datos.nombres(), datos.apellidos())
                .allMatch(UtilObjeto::esNulo);
    }

    private static String recortarSiLlega(String texto) {
        return UtilObjeto.esNulo(texto) ? texto : UtilTexto.aplicarTrim(texto);
    }
}
