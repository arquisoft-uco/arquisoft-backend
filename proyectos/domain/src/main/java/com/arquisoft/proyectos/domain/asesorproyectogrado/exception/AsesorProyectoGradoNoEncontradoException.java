package com.arquisoft.proyectos.domain.asesorproyectogrado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;

import java.util.UUID;

public final class AsesorProyectoGradoNoEncontradoException extends DomainException {

    public AsesorProyectoGradoNoEncontradoException(UUID asesor, UUID proyectoGrado) {
        super(
                Mensajes.formatear(AsesorProyectoGradoKey.ERROR_NO_ENCONTRADO, asesor, proyectoGrado),
                ProyectosCodes.AsesorProyectoGrado.ASESOR_PROYECTO_GRADO_NO_ENCONTRADO
        );
    }
}
