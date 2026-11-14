package com.arquisoft.entregables.infrastructure.paqueteentregable.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.PaqueteEntregableKey;

import java.util.UUID;

public final class ArchivoArtefactoNoDisponibleException extends InfrastructureException {

    public ArchivoArtefactoNoDisponibleException(UUID artefacto, String nombreDocumento, String claveObjeto) {
        super(BaseError.conDetalle(
                EntregablesCodes.PaqueteEntregable.ARCHIVO_ARTEFACTO_NO_DISPONIBLE,
                Mensajes.formatear(PaqueteEntregableKey.ERROR_ARCHIVO_ARTEFACTO_NO_DISPONIBLE, nombreDocumento),
                Mensajes.formatear(
                        PaqueteEntregableKey.ERROR_ARCHIVO_ARTEFACTO_NO_DISPONIBLE_DETALLE, claveObjeto, artefacto)
        ));
    }
}
