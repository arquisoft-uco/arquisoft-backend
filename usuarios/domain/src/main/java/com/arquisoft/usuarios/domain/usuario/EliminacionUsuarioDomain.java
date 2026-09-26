package com.arquisoft.usuarios.domain.usuario;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EliminacionUsuarioDomain {

    private UUID usuario;

    private EliminacionUsuarioDomain() {}

    public static EliminacionUsuarioDomain crear(UUID usuario) {
        var eliminacion = new EliminacionUsuarioDomain();
        var result = new ValidationResult();

        eliminacion.setUsuario(usuario, result);

        result.lanzarSiTieneErrores();
        return eliminacion;
    }

    private void setUsuario(UUID usuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(usuario, UsuariosFields.Usuario.USUARIO,
                UsuariosCodes.Usuario.USUARIO_REQUERIDO, result)) {
            return;
        }
        this.usuario = usuario;
    }

    public UUID getUsuario() {
        return usuario;
    }
}
