package com.arquisoft.biblioteca.domain.entregableproyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.key.biblioteca.EntregableProyectoGradoKey;

import java.util.UUID;

public final class EntregableProyectoGradoNoEncontradoException extends DomainException {

    public EntregableProyectoGradoNoEncontradoException(UUID entregableProyectoGrado) {
        super(BaseError.conDetalle(
                BibliotecaCodes.EntregableProyectoGrado.NO_ENCONTRADO,
                Mensajes.obtener(EntregableProyectoGradoKey.ERROR_NO_ENCONTRADO),
                Mensajes.formatear(EntregableProyectoGradoKey.ERROR_NO_ENCONTRADO_DETALLE, entregableProyectoGrado)
        ));
    }
}
