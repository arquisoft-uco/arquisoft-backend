package com.arquisoft.entregables.infrastructure.artefacto.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.ArtefactoKey;

import java.util.UUID;

public final class ArchivoOrigenArtefactoNoDisponibleException extends InfrastructureException {

    public ArchivoOrigenArtefactoNoDisponibleException(UUID artefacto, String nombreDocumento, String claveOrigen) {
        super(BaseError.conDetalle(
                EntregablesCodes.Artefacto.ARCHIVO_ORIGEN_NO_DISPONIBLE,
                Mensajes.formatear(ArtefactoKey.ERROR_ARCHIVO_ORIGEN_NO_DISPONIBLE, nombreDocumento),
                Mensajes.formatear(ArtefactoKey.ERROR_ARCHIVO_ORIGEN_NO_DISPONIBLE_DETALLE, claveOrigen, artefacto)
        ));
    }
}
