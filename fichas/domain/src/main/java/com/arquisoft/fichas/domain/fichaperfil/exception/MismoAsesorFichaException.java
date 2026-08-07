package com.arquisoft.fichas.domain.fichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;

import java.util.UUID;

public final class MismoAsesorFichaException extends DomainException {

    public MismoAsesorFichaException(UUID asesorFichaActual) {
        super(
                Mensajes.formatear(FichaPerfilKey.ERROR_MISMO_ASESOR, asesorFichaActual),
                FichasCodes.FichaPerfil.MISMO_ASESOR
        );
    }
}
