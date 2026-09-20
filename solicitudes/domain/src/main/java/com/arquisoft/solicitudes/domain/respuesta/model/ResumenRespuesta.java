package com.arquisoft.solicitudes.domain.respuesta.model;

import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public record ResumenRespuesta(UUID solicitud, String estado) {

    public static final ResumenRespuesta VACIO =
            new ResumenRespuesta(UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO);

    public boolean esVacio() {
        return this.equals(VACIO);
    }
}
