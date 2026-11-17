package com.arquisoft.biblioteca.domain.estudianteproyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.key.biblioteca.EstudianteProyectoGradoKey;

import java.util.UUID;

public final class EntregableSinAutoresException extends DomainException {

    public EntregableSinAutoresException(UUID entregableProyectoGrado) {
        super(BaseError.conDetalle(
                BibliotecaCodes.EstudianteProyectoGrado.SIN_AUTORES,
                Mensajes.obtener(EstudianteProyectoGradoKey.ERROR_SIN_AUTORES),
                Mensajes.formatear(EstudianteProyectoGradoKey.ERROR_SIN_AUTORES_DETALLE, entregableProyectoGrado)
        ));
    }
}
