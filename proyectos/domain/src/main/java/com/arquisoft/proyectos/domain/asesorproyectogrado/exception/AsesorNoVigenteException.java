package com.arquisoft.proyectos.domain.asesorproyectogrado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;

import java.util.UUID;

public final class AsesorNoVigenteException extends DomainException {

    public AsesorNoVigenteException(UUID asesor) {
        super(
                Mensajes.formatear(AsesorProyectoGradoKey.ERROR_ASESOR_NO_VIGENTE, asesor),
                ProyectosCodes.AsesorProyectoGrado.ASESOR_NO_VIGENTE
        );
    }
}
