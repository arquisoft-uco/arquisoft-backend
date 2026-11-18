package com.arquisoft.artefactos.domain.proyectogrado.exception;

import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;

public final class ProyectoGradoNoConcluidoException extends DomainException {

    public ProyectoGradoNoConcluidoException(EstadoProyectoGrado estado) {
        super(BaseError.conDetalle(
                ArtefactosCodes.ProyectoGrado.NO_CONCLUIDO,
                Mensajes.formatear(ProyectoGradoKey.ERROR_NO_CONCLUIDO, estado.nombre()),
                Mensajes.formatear(ProyectoGradoKey.ERROR_NO_CONCLUIDO_DETALLE, estado.proyectoGrado())
        ));
    }
}
