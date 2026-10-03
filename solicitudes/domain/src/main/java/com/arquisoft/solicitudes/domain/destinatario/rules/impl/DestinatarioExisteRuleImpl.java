package com.arquisoft.solicitudes.domain.destinatario.rules.impl;

import com.arquisoft.solicitudes.domain.destinatario.exception.DestinatarioNoEncontradoException;
import com.arquisoft.solicitudes.domain.destinatario.model.ExistenciaDestinatario;
import com.arquisoft.solicitudes.domain.destinatario.rules.DestinatarioExisteRule;

public class DestinatarioExisteRuleImpl implements DestinatarioExisteRule {

    @Override
    public void validar(ExistenciaDestinatario existencia) {
        if (existencia.destinatario().esVacio()) {
            throw new DestinatarioNoEncontradoException(existencia.usuario());
        }
    }
}
