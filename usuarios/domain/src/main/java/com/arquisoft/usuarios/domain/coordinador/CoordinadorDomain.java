package com.arquisoft.usuarios.domain.coordinador;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class CoordinadorDomain {

    private UUID usuario;

    private CoordinadorDomain() {}

    public static CoordinadorDomain crear(UUID usuario) {
        var coordinador = new CoordinadorDomain();
        var result = new ValidationResult();

        coordinador.setUsuario(usuario, result);

        result.lanzarSiTieneErrores();
        return coordinador;
    }

    public static CoordinadorDomain reconstruir(UUID usuario) {
        var coordinador = new CoordinadorDomain();
        coordinador.usuario = usuario;
        return coordinador;
    }

    private void setUsuario(UUID usuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(usuario,
                UsuariosFields.Coordinador.USUARIO,
                UsuariosCodes.Coordinador.USUARIO_REQUERIDO, result)) {
            return;
        }
        this.usuario = usuario;
    }

    public UUID getUsuario() {
        return usuario;
    }
}
