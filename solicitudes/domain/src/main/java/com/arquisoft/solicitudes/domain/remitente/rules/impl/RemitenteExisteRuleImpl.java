package com.arquisoft.solicitudes.domain.remitente.rules.impl;

import com.arquisoft.solicitudes.domain.remitente.exception.RemitenteNoEncontradoException;
import com.arquisoft.solicitudes.domain.remitente.model.ExistenciaRemitente;
import com.arquisoft.solicitudes.domain.remitente.rules.RemitenteExisteRule;

public class RemitenteExisteRuleImpl implements RemitenteExisteRule {

    @Override
    public void validar(ExistenciaRemitente existencia) {
        if (existencia.remitente().esVacio()) {
            throw new RemitenteNoEncontradoException(existencia.usuario());
        }
    }
}
