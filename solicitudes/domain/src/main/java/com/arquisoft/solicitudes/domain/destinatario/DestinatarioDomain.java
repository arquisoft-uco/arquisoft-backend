package com.arquisoft.solicitudes.domain.destinatario;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class DestinatarioDomain {

    private UUID id;
    private UUID usuario;

    private DestinatarioDomain() {}

    private DestinatarioDomain(UUID id, UUID usuario) {
        this.id = id;
        this.usuario = usuario;
    }

    public static DestinatarioDomain crear(UUID usuario) {
        var destinatario = new DestinatarioDomain();
        var result = new ValidationResult();

        destinatario.setId();
        destinatario.setUsuario(usuario, result);

        result.lanzarSiTieneErrores();
        return destinatario;
    }

    public static DestinatarioDomain reconstruir(UUID id, UUID usuario) {
        return new DestinatarioDomain(id, usuario);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setUsuario(UUID usuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(usuario,
                SolicitudesFields.Destinatario.USUARIO,
                SolicitudesCodes.Destinatario.USUARIO_REQUERIDO, result)) {
            return;
        }
        this.usuario = usuario;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuario() {
        return usuario;
    }
}
