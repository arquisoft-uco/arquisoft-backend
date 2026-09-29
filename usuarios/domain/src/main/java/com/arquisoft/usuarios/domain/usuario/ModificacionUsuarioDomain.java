package com.arquisoft.usuarios.domain.usuario;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.message.constant.UsuariosLimits;
import com.arquisoft.shared.message.key.usuarios.RegistrarUsuarioKey;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;

public final class ModificacionUsuarioDomain {

    private UUID usuario;
    private String identificador;
    private String nombre;
    private String email;
    private String contacto;
    private String nombres;
    private String apellidos;
    private List<String> roles;

    public record DatosModificacionUsuario(String identificador, String nombre, String email,
                                           String contacto, String nombres, String apellidos) {}

    private ModificacionUsuarioDomain() {}

    public static ModificacionUsuarioDomain crear(UUID usuario, DatosModificacionUsuario datos,
                                                  List<String> roles) {
        var modificacion = new ModificacionUsuarioDomain();
        var result = new ValidationResult();

        modificacion.setUsuario(usuario, result);
        modificacion.setIdentificador(datos.identificador(), result);
        modificacion.setNombre(datos.nombre(), result);
        modificacion.setEmail(datos.email(), result);
        modificacion.setContacto(datos.contacto(), result);
        modificacion.setNombres(datos.nombres(), result);
        modificacion.setApellidos(datos.apellidos(), result);
        modificacion.setRoles(roles);

        result.lanzarSiTieneErrores();
        return modificacion;
    }

    private void setUsuario(UUID usuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(usuario, UsuariosFields.Usuario.USUARIO,
                UsuariosCodes.Usuario.USUARIO_REQUERIDO, result)) {
            return;
        }
        this.usuario = usuario;
    }

    private void setIdentificador(String identificador, ValidationResult result) {
        if (UtilObjeto.esNulo(identificador)) {
            return;
        }
        if (!ValidatorTexto.noEnBlanco(identificador, UsuariosFields.Usuario.IDENTIFICADOR,
                UsuariosCodes.Usuario.IDENTIFICADOR_REQUERIDO, result)) {
            return;
        }
        var identificadorRecortado = UtilTexto.aplicarTrim(identificador);
        if (!ValidatorLongitud.longitudEntre(identificadorRecortado, UsuariosLimits.Usuario.IDENTIFICADOR_MIN,
                UsuariosLimits.Usuario.IDENTIFICADOR_MAX, UsuariosFields.Usuario.IDENTIFICADOR,
                UsuariosCodes.Usuario.IDENTIFICADOR_LONGITUD, result)) {
            return;
        }
        this.identificador = identificadorRecortado;
    }

    private void setNombre(String nombre, ValidationResult result) {
        if (UtilObjeto.esNulo(nombre)) {
            return;
        }
        if (!ValidatorTexto.noEnBlanco(nombre, UsuariosFields.Usuario.NOMBRE,
                UsuariosCodes.Usuario.NOMBRE_REQUERIDO, result)) {
            return;
        }
        var nombreRecortado = UtilTexto.aplicarTrim(nombre);
        if (!ValidatorLongitud.longitudEntre(nombreRecortado, UsuariosLimits.Usuario.NOMBRE_MIN,
                UsuariosLimits.Usuario.NOMBRE_MAX, UsuariosFields.Usuario.NOMBRE,
                UsuariosCodes.Usuario.NOMBRE_LONGITUD, result)) {
            return;
        }
        if (!UtilTexto.coincidePatron(nombreRecortado, UsuariosLimits.Usuario.PATRON_NOMBRE)) {
            result.agregarError(UsuariosFields.Usuario.NOMBRE, UsuariosCodes.Usuario.NOMBRE_FORMATO,
                    Mensajes.formatear(RegistrarUsuarioKey.ERROR_NOMBRE_FORMATO, nombreRecortado));
            return;
        }
        this.nombre = nombreRecortado;
    }

    private void setEmail(String email, ValidationResult result) {
        if (UtilObjeto.esNulo(email)) {
            return;
        }
        if (!ValidatorTexto.noEnBlanco(email, UsuariosFields.Usuario.EMAIL,
                UsuariosCodes.Usuario.EMAIL_REQUERIDO, result)) {
            return;
        }
        var emailNormalizado = UtilTexto.aplicarTrim(email).toLowerCase(Locale.ROOT);
        if (!ValidatorTexto.correoValido(emailNormalizado, UsuariosFields.Usuario.EMAIL,
                UsuariosCodes.Usuario.EMAIL_FORMATO, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudEntre(emailNormalizado, UsuariosLimits.Usuario.EMAIL_MIN,
                UsuariosLimits.Usuario.EMAIL_MAX, UsuariosFields.Usuario.EMAIL,
                UsuariosCodes.Usuario.EMAIL_LONGITUD, result)) {
            return;
        }
        this.email = emailNormalizado;
    }

    private void setContacto(String contacto, ValidationResult result) {
        if (UtilObjeto.esNulo(contacto)) {
            return;
        }
        if (!ValidatorTexto.noEnBlanco(contacto, UsuariosFields.Usuario.CONTACTO,
                UsuariosCodes.Usuario.CONTACTO_REQUERIDO, result)) {
            return;
        }
        var contactoRecortado = UtilTexto.aplicarTrim(contacto);
        if (!UtilTexto.coincidePatron(contactoRecortado, UsuariosLimits.Usuario.PATRON_CONTACTO)) {
            result.agregarError(UsuariosFields.Usuario.CONTACTO, UsuariosCodes.Usuario.CONTACTO_FORMATO,
                    Mensajes.formatear(RegistrarUsuarioKey.ERROR_CONTACTO_FORMATO, contactoRecortado));
            return;
        }
        if (!ValidatorLongitud.longitudEntre(contactoRecortado, UsuariosLimits.Usuario.CONTACTO_MIN,
                UsuariosLimits.Usuario.CONTACTO_MAX, UsuariosFields.Usuario.CONTACTO,
                UsuariosCodes.Usuario.CONTACTO_LONGITUD, result)) {
            return;
        }
        this.contacto = contactoRecortado;
    }

    private void setNombres(String nombres, ValidationResult result) {
        if (UtilObjeto.esNulo(nombres)) {
            return;
        }
        if (!ValidatorTexto.noEnBlanco(nombres, UsuariosFields.Usuario.NOMBRES,
                UsuariosCodes.Usuario.NOMBRES_REQUERIDO, result)) {
            return;
        }
        this.nombres = UtilTexto.aplicarTrim(nombres);
    }

    private void setApellidos(String apellidos, ValidationResult result) {
        if (UtilObjeto.esNulo(apellidos)) {
            return;
        }
        if (!ValidatorTexto.noEnBlanco(apellidos, UsuariosFields.Usuario.APELLIDOS,
                UsuariosCodes.Usuario.APELLIDOS_REQUERIDO, result)) {
            return;
        }
        this.apellidos = UtilTexto.aplicarTrim(apellidos);
    }

    private void setRoles(List<String> roles) {
        this.roles = UtilColeccion.aplicarPorDefecto(roles);
    }

    public boolean modificaDatos() {
        return Stream.of(identificador, nombre, email, contacto, nombres, apellidos)
                .anyMatch(UtilObjeto::noEsNulo);
    }

    public boolean modificaIdentidad() {
        return Stream.of(email, nombres, apellidos).anyMatch(UtilObjeto::noEsNulo);
    }

    public boolean contieneRol(String rol) {
        return roles.contains(rol);
    }

    public UUID getUsuario() {
        return usuario;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getContacto() {
        return contacto;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public List<String> getRoles() {
        return roles;
    }
}
