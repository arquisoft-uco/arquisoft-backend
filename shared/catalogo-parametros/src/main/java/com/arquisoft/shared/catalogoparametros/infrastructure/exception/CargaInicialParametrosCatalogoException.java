package com.arquisoft.shared.catalogoparametros.infrastructure.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.key.app.CatalogoParametrosKey;

public final class CargaInicialParametrosCatalogoException extends InfrastructureException {

    public CargaInicialParametrosCatalogoException(String motivo) {
        super(BaseError.conDetalle(
                AppCodes.CatalogoParametros.CARGA_INICIAL_FALLIDA,
                Mensajes.obtener(CatalogoParametrosKey.ERROR_CARGA_INICIAL_FALLIDA),
                Mensajes.formatear(CatalogoParametrosKey.ERROR_CARGA_INICIAL_FALLIDA_DETALLE, motivo)));
    }
}
