package com.arquisoft.fichas.domain.fichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.exception.ApplicationException;

import java.util.UUID;

public final class AsesorFichaNoEncontradoException extends ApplicationException {

    public AsesorFichaNoEncontradoException(UUID id) {
        super(
                Messages.formatear(FichaPerfilKey.ERROR_ASESOR_NO_ENCONTRADO, id),
                FichasCodes.FichaPerfil.ASESOR_NO_ENCONTRADO
        );
    }
}
