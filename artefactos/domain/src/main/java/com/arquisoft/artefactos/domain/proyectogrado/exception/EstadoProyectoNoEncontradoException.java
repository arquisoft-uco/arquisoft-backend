package com.arquisoft.artefactos.domain.proyectogrado.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;

public final class EstadoProyectoNoEncontradoException extends DomainException {

    public EstadoProyectoNoEncontradoException(String id) {
        super(BaseError.conDetalle(
                ArtefactosCodes.ProyectoGrado.ESTADO_PROYECTO_NO_ENCONTRADO,
                Mensajes.obtener(ProyectoGradoKey.ERROR_ESTADO_NO_ENCONTRADO),
                Mensajes.formatear(ProyectoGradoKey.ERROR_ESTADO_NO_ENCONTRADO_DETALLE, id)
        ));
    }
}
