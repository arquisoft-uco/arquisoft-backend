package com.arquisoft.solicitudes.domain.remitente;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RemitenteDomain {

    private UUID id;
    private UUID usuario;

    private RemitenteDomain() {}

    private RemitenteDomain(UUID id, UUID usuario) {
        this.id = id;
        this.usuario = usuario;
    }

    public static RemitenteDomain crear(UUID usuario) {
        var remitente = new RemitenteDomain();
        var result = new ValidationResult();

        remitente.setId();
        remitente.setUsuario(usuario, result);

        result.lanzarSiTieneErrores();
        return remitente;
    }

    public static RemitenteDomain reconstruir(UUID id, UUID usuario) {
        return new RemitenteDomain(id, usuario);
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setUsuario(UUID usuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(usuario,
                SolicitudesFields.Remitente.USUARIO,
                SolicitudesCodes.Remitente.USUARIO_REQUERIDO, result)) {
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
