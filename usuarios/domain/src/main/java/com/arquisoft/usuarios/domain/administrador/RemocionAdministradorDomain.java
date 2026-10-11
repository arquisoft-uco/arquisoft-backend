package com.arquisoft.usuarios.domain.administrador;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RemocionAdministradorDomain {

    private UUID usuario;
    private UUID actor;

    private RemocionAdministradorDomain() {}

    public static RemocionAdministradorDomain crear(UUID usuario, UUID actor) {
        var remocion = new RemocionAdministradorDomain();
        var result = new ValidationResult();

        remocion.setUsuario(usuario, result);
        remocion.setActor(actor, result);

        result.lanzarSiTieneErrores();
        return remocion;
    }

    private void setUsuario(UUID usuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(usuario, UsuariosFields.Administrador.USUARIO,
                UsuariosCodes.Administrador.USUARIO_REQUERIDO, result)) {
            return;
        }
        this.usuario = usuario;
    }

    private void setActor(UUID actor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(actor, UsuariosFields.Administrador.ACTOR,
                UsuariosCodes.Administrador.ACTOR_REQUERIDO, result)) {
            return;
        }
        this.actor = actor;
    }

    public UUID getUsuario() {
        return usuario;
    }

    public UUID getActor() {
        return actor;
    }
}
