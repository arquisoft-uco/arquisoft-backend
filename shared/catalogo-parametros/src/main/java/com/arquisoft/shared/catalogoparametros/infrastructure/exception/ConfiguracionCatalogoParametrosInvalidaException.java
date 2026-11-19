package com.arquisoft.shared.catalogoparametros.infrastructure.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.key.app.CatalogoParametrosKey;

public final class ConfiguracionCatalogoParametrosInvalidaException extends InfrastructureException {

    public ConfiguracionCatalogoParametrosInvalidaException(String propiedades) {
        super(BaseError.of(
                AppCodes.CatalogoParametros.CONFIGURACION_INVALIDA,
                Mensajes.formatear(CatalogoParametrosKey.ERROR_CONFIGURACION_INVALIDA, propiedades)));
    }
}
