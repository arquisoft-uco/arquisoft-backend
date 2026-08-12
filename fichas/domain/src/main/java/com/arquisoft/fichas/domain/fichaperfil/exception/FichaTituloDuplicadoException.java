package com.arquisoft.fichas.domain.fichaperfil.exception;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.exception.DomainException;

public final class FichaTituloDuplicadoException extends DomainException {

    public FichaTituloDuplicadoException(String titulo) {
        super(
                Mensajes.formatear(FichaPerfilKey.ERROR_TITULO_DUPLICADO, titulo),
                FichasCodes.FichaPerfil.FICHA_TITULO_DUPLICADO
        );
    }
}
