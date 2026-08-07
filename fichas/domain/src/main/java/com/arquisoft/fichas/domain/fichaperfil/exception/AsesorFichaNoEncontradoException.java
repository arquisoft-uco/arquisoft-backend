package com.arquisoft.fichas.domain.fichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.ApplicationException;

import java.util.UUID;

public final class AsesorFichaNoEncontradoException extends ApplicationException {

    public AsesorFichaNoEncontradoException(UUID id) {
        super(
                Mensajes.formatear(FichaPerfilKey.ERROR_ASESOR_NO_ENCONTRADO, id),
                FichasCodes.FichaPerfil.ASESOR_NO_ENCONTRADO
        );
    }
}
