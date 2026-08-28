package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.RemitenteNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaRemitente;
import com.arquisoft.solicitudes.domain.solicitud.rules.RemitenteExisteRule;

public class RemitenteExisteRuleImpl implements RemitenteExisteRule {

    @Override
    public void validar(ExistenciaRemitente existencia) {
        if (!existencia.existe()) {
            throw new RemitenteNoEncontradoException(existencia.usuario());
        }
    }
}
