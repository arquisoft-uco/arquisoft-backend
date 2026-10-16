package com.arquisoft.fichas.domain.estadofichaperfil.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;

import java.util.UUID;

public final class FichaPerfilSinEstudiantesVigentesException extends DomainException {

    public FichaPerfilSinEstudiantesVigentesException(UUID fichaPerfil) {
        super(
                Mensajes.formatear(EstadoFichaPerfilKey.ERROR_SIN_ESTUDIANTES_VIGENTES, fichaPerfil),
                FichasCodes.EstadoFichaPerfil.SIN_ESTUDIANTES_VIGENTES
        );
    }
}
