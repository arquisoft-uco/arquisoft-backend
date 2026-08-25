package com.arquisoft.usuarios.domain.usuario;

import com.arquisoft.shared.events.AggregateRoot;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.usuarios.domain.usuario.event.UsuarioCreadoEvent;
import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;

import java.util.UUID;

public final class UsuarioDomain extends AggregateRoot {

    private UUID id;
    private String email;
    private UsuarioRole rol;

    private UsuarioDomain() {}

    public static UsuarioDomain crear(String email, UsuarioRole rol) {
        var usuario = new UsuarioDomain();
        var result = new ValidationResult();

        usuario.setId();
        usuario.setEmail(email, result);
        usuario.setRol(rol, result);

        result.lanzarSiTieneErrores();

        usuario.publicarEvento(
                new UsuarioCreadoEvent(usuario.id, usuario.email, usuario.rol.getCodigo()));
        return usuario;
    }

    public static UsuarioDomain reconstruir(UUID id, String email, UsuarioRole rol) {
        var usuario = new UsuarioDomain();
        usuario.id = id;
        usuario.email = email;
        usuario.rol = rol;
        return usuario;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setEmail(String email, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(email,
                UsuariosFields.Usuario.EMAIL,
                UsuariosCodes.Usuario.EMAIL_REQUERIDO, result)) {
            return;
        }

        var normalizado = UtilTexto.aplicarTrim(email).toLowerCase();

        if (!ValidatorTexto.correoValido(normalizado,
                UsuariosFields.Usuario.EMAIL,
                UsuariosCodes.Usuario.EMAIL_REQUERIDO, result)) {
            return;
        }
        this.email = normalizado;
    }

    private void setRol(UsuarioRole rol, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(rol,
                UsuariosFields.Usuario.ROL,
                UsuariosCodes.Usuario.ROL_REQUERIDO, result)) {
            return;
        }
        this.rol = rol;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UsuarioRole getRol() {
        return rol;
    }
}
