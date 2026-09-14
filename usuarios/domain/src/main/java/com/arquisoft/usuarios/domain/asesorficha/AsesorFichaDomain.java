package com.arquisoft.usuarios.domain.asesorficha;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class AsesorFichaDomain {

    private UUID usuario;

    private AsesorFichaDomain() {}

    public static AsesorFichaDomain crear(UUID usuario) {
        var asesorFicha = new AsesorFichaDomain();
        var result = new ValidationResult();

        asesorFicha.setUsuario(usuario, result);

        result.lanzarSiTieneErrores();
        return asesorFicha;
    }

    public static AsesorFichaDomain reconstruir(UUID usuario) {
        var asesorFicha = new AsesorFichaDomain();
        asesorFicha.usuario = usuario;
        return asesorFicha;
    }

    private void setUsuario(UUID usuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(usuario,
                UsuariosFields.AsesorFicha.USUARIO,
                UsuariosCodes.AsesorFicha.USUARIO_REQUERIDO, result)) {
            return;
        }
        this.usuario = usuario;
    }

    public UUID getUsuario() {
        return usuario;
    }
}
