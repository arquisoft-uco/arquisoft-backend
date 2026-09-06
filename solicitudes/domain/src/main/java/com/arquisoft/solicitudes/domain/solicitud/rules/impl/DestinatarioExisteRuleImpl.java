package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.DestinatarioNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaDestinatario;
import com.arquisoft.solicitudes.domain.solicitud.rules.DestinatarioExisteRule;

public class DestinatarioExisteRuleImpl implements DestinatarioExisteRule {

    @Override
    public void validar(ExistenciaDestinatario existencia) {
        if (!existencia.existe()) {
            throw new DestinatarioNoEncontradoException(existencia.usuario());
        }
    }
}
