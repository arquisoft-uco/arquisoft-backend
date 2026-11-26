package com.arquisoft.shared.catalogoparametros.infrastructure.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.key.app.CatalogoParametrosKey;

public final class ParametroCatalogoNoDisponibleException extends InfrastructureException {

    public ParametroCatalogoNoDisponibleException(String nombre) {
        super(BaseError.conDetalle(
                AppCodes.CatalogoParametros.NO_DISPONIBLE,
                Mensajes.obtener(CatalogoParametrosKey.ERROR_NO_DISPONIBLE),
                Mensajes.formatear(CatalogoParametrosKey.ERROR_NO_DISPONIBLE_DETALLE, nombre)));
    }
}
