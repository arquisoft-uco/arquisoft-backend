package com.arquisoft.artefactos.domain.proyectogrado.exception;

import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;

public final class ProyectoGradoCongeladoException extends DomainException {

    public ProyectoGradoCongeladoException(EstadoProyectoGrado estado) {
        super(BaseError.conDetalle(
                ArtefactosCodes.ProyectoGrado.CONGELADO,
                Mensajes.formatear(ProyectoGradoKey.ERROR_CONGELADO, estado.nombre()),
                Mensajes.formatear(ProyectoGradoKey.ERROR_CONGELADO_DETALLE, estado.proyectoGrado())
        ));
    }
}
