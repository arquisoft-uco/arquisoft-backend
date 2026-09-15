package com.arquisoft.usuarios.domain.asesor;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class AsesorDomain {

    private UUID usuario;

    private AsesorDomain() {}

    public static AsesorDomain crear(UUID usuario) {
        var asesor = new AsesorDomain();
        var result = new ValidationResult();

        asesor.setUsuario(usuario, result);

        result.lanzarSiTieneErrores();
        return asesor;
    }

    private void setUsuario(UUID usuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(usuario,
                UsuariosFields.Asesor.USUARIO,
                UsuariosCodes.Asesor.USUARIO_REQUERIDO, result)) {
            return;
        }
        this.usuario = usuario;
    }

    public UUID getUsuario() {
        return usuario;
    }
}
