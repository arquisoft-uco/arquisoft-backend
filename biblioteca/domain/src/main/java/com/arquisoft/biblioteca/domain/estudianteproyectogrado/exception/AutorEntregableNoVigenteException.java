package com.arquisoft.biblioteca.domain.estudianteproyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.key.biblioteca.EstudianteProyectoGradoKey;

import java.util.List;
import java.util.UUID;

public final class AutorEntregableNoVigenteException extends DomainException {

    public AutorEntregableNoVigenteException(UUID entregableProyectoGrado, List<UUID> autores) {
        super(BaseError.conDetalle(
                BibliotecaCodes.EstudianteProyectoGrado.AUTOR_NO_VIGENTE,
                Mensajes.obtener(EstudianteProyectoGradoKey.ERROR_AUTOR_NO_VIGENTE),
                Mensajes.formatear(EstudianteProyectoGradoKey.ERROR_AUTOR_NO_VIGENTE_DETALLE,
                        entregableProyectoGrado, autores)
        ));
    }
}
