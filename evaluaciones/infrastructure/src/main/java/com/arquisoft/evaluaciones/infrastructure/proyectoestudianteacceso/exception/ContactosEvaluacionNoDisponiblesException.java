package com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.exception;

import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.key.evaluaciones.ProyeccionAccesoEvaluacionKey;

public final class ContactosEvaluacionNoDisponiblesException extends InfrastructureException {

    public ContactosEvaluacionNoDisponiblesException() {
        super(
                Mensajes.formatear(ProyeccionAccesoEvaluacionKey.ERROR_CONTACTOS_NO_DISPONIBLES),
                EvaluacionesCodes.ContactoUsuario.CONTACTOS_NO_DISPONIBLES
        );
    }
}
