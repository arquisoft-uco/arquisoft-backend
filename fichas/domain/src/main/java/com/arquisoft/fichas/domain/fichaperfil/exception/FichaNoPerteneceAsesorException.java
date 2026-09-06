package com.arquisoft.fichas.domain.fichaperfil.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;

import java.util.UUID;

public final class FichaNoPerteneceAsesorException extends DomainException {

    public FichaNoPerteneceAsesorException(UUID fichaPerfil, UUID asesorFicha) {
        super(
                Mensajes.formatear(FichaPerfilKey.ERROR_NO_PERTENECE_ASESOR, asesorFicha, fichaPerfil),
                FichasCodes.FichaPerfil.FICHA_NO_PERTENECE_ASESOR
        );
    }
}
