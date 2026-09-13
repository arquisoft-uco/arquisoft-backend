package com.arquisoft.usuarios.domain.estudiante;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EstudianteDomain {

    private UUID usuario;

    private EstudianteDomain() {}

    public static EstudianteDomain crear(UUID usuario) {
        var estudiante = new EstudianteDomain();
        var result = new ValidationResult();

        estudiante.setUsuario(usuario, result);

        result.lanzarSiTieneErrores();
        return estudiante;
    }

    public static EstudianteDomain reconstruir(UUID usuario) {
        var estudiante = new EstudianteDomain();
        estudiante.usuario = usuario;
        return estudiante;
    }

    private void setUsuario(UUID usuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(usuario,
                UsuariosFields.Estudiante.USUARIO,
                UsuariosCodes.Estudiante.USUARIO_REQUERIDO, result)) {
            return;
        }
        this.usuario = usuario;
    }

    public UUID getUsuario() {
        return usuario;
    }
}
