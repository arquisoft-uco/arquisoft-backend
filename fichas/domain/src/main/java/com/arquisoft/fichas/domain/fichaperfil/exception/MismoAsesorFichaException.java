package com.arquisoft.fichas.domain.fichaperfil.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;

import java.util.UUID;

public final class MismoAsesorFichaException extends DomainException {

    public MismoAsesorFichaException(UUID asesorFichaActual) {
        super(
                Messages.formatear(FichasKeys.FichaPerfil.ERROR_MISMO_ASESOR, asesorFichaActual),
                FichasCodes.FichaPerfil.MISMO_ASESOR
        );
    }
}
