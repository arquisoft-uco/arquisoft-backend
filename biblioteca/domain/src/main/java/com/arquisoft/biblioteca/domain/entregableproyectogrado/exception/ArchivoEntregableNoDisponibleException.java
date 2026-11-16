package com.arquisoft.biblioteca.domain.entregableproyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.key.biblioteca.EntregableProyectoGradoKey;

import java.util.UUID;

public final class ArchivoEntregableNoDisponibleException extends DomainException {

    public ArchivoEntregableNoDisponibleException(UUID entregableProyectoGrado) {
        super(BaseError.conDetalle(
                BibliotecaCodes.EntregableProyectoGrado.ARCHIVO_NO_DISPONIBLE,
                Mensajes.obtener(EntregableProyectoGradoKey.ERROR_ARCHIVO_NO_DISPONIBLE),
                Mensajes.formatear(EntregableProyectoGradoKey.ERROR_ARCHIVO_NO_DISPONIBLE_DETALLE, entregableProyectoGrado)
        ));
    }
}
