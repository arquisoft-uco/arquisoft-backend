package com.arquisoft.entregables.domain.paqueteentregable.exception;

import com.arquisoft.entregables.domain.paqueteentregable.model.VigenciaPaqueteArmado;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.key.entregables.PaqueteEntregableKey;

public final class PaqueteArmadoDesactualizadoException extends DomainException {

    public PaqueteArmadoDesactualizadoException(VigenciaPaqueteArmado vigencia) {
        super(BaseError.conDetalle(
                EntregablesCodes.PaqueteEntregable.DESACTUALIZADO,
                Mensajes.formatear(PaqueteEntregableKey.ERROR_DESACTUALIZADO, vigencia.nombreProyecto()),
                Mensajes.formatear(PaqueteEntregableKey.ERROR_DESACTUALIZADO_DETALLE,
                        vigencia.proyectoGrado(), vigencia.versionEmpaquetada(), vigencia.versionActual(),
                        vigencia.versionesEmpaquetadas().size(), vigencia.versionesActuales().size())
        ));
    }
}
