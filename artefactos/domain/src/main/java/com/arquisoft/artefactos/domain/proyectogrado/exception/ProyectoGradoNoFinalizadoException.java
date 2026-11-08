package com.arquisoft.artefactos.domain.proyectogrado.exception;

import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;

public final class ProyectoGradoNoFinalizadoException extends DomainException {

    public ProyectoGradoNoFinalizadoException(EstadoProyectoGrado estado) {
        super(BaseError.conDetalle(
                ArtefactosCodes.ProyectoGrado.NO_FINALIZADO,
                Mensajes.formatear(ProyectoGradoKey.ERROR_NO_FINALIZADO, estado.nombre()),
                Mensajes.formatear(ProyectoGradoKey.ERROR_NO_FINALIZADO_DETALLE, estado.proyectoGrado())
        ));
    }
}
