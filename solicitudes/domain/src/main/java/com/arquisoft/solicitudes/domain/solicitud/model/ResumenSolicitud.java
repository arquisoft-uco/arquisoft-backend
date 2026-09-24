package com.arquisoft.solicitudes.domain.solicitud.model;

import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public record ResumenSolicitud(UUID solicitud, UUID remitenteUsuario, String tipoSolicitud) {

    public static final ResumenSolicitud VACIO = new ResumenSolicitud(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO);

    public boolean esVacio() {
        return this.equals(VACIO);
    }
}
