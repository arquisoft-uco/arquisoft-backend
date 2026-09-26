package com.arquisoft.usuarios.domain.usuario;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;

import java.util.UUID;

public final class CambioEstadoUsuarioDomain {

    private UUID usuario;
    private EstadoUsuario estado;

    private CambioEstadoUsuarioDomain() {}

    public static CambioEstadoUsuarioDomain crear(UUID usuario, EstadoUsuario estado) {
        var cambio = new CambioEstadoUsuarioDomain();
        var result = new ValidationResult();

        cambio.setUsuario(usuario, result);
        cambio.setEstado(estado, result);

        result.lanzarSiTieneErrores();
        return cambio;
    }

    private void setUsuario(UUID usuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(usuario, UsuariosFields.Usuario.USUARIO,
                UsuariosCodes.Usuario.USUARIO_REQUERIDO, result)) {
            return;
        }
        this.usuario = usuario;
    }

    private void setEstado(EstadoUsuario estado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estado, UsuariosFields.Usuario.ESTADO,
                UsuariosCodes.Usuario.ESTADO_REQUERIDO, result)) {
            return;
        }
        this.estado = estado;
    }

    public UUID getUsuario() {
        return usuario;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }
}
