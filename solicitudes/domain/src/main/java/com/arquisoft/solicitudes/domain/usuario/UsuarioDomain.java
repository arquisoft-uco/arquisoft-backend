package com.arquisoft.solicitudes.domain.usuario;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class UsuarioDomain {

    private UUID id;
    private String identificador;
    private String nombre;
    private String email;

    private UsuarioDomain() {}

    private UsuarioDomain(UUID id, String identificador, String nombre, String email) {
        this.id = id;
        this.identificador = identificador;
        this.nombre = nombre;
        this.email = email;
    }

    public static UsuarioDomain crear(UUID id, String identificador, String nombre, String email) {
        var usuario = new UsuarioDomain();
        var result = new ValidationResult();

        usuario.setId(id, result);
        usuario.setIdentificador(identificador, result);
        usuario.setNombre(nombre, result);
        usuario.setEmail(email, result);

        result.lanzarSiTieneErrores();
        return usuario;
    }

    public static UsuarioDomain reconstruir(UUID id, String identificador, String nombre, String email) {
        return new UsuarioDomain(id, identificador, nombre, email);
    }

    private void setId(UUID id, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(id,
                SolicitudesFields.Usuario.ID,
                SolicitudesCodes.Usuario.ID_REQUERIDO, result)) {
            return;
        }
        this.id = id;
    }

    private void setIdentificador(String identificador, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(identificador,
                SolicitudesFields.Usuario.IDENTIFICADOR,
                SolicitudesCodes.Usuario.IDENTIFICADOR_REQUERIDO, result)) {
            return;
        }
        this.identificador = identificador;
    }

    private void setNombre(String nombre, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(nombre,
                SolicitudesFields.Usuario.NOMBRE,
                SolicitudesCodes.Usuario.NOMBRE_REQUERIDO, result)) {
            return;
        }
        this.nombre = nombre;
    }

    private void setEmail(String email, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(email,
                SolicitudesFields.Usuario.EMAIL,
                SolicitudesCodes.Usuario.EMAIL_REQUERIDO, result)) {
            return;
        }
        this.email = email;
    }

    public UUID getId() {
        return id;
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
}
