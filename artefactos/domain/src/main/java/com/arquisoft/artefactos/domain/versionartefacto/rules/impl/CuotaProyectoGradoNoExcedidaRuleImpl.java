package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.CuotaProyectoGradoExcedidaException;
import com.arquisoft.artefactos.domain.versionartefacto.model.CuotaProyectoGrado;
import com.arquisoft.artefactos.domain.versionartefacto.rules.CuotaProyectoGradoNoExcedidaRule;

public class CuotaProyectoGradoNoExcedidaRuleImpl implements CuotaProyectoGradoNoExcedidaRule {

    @Override
    public void validar(CuotaProyectoGrado cuota) {
        if (cuota.usoBytes() + cuota.tamanoNuevoBytes() > cuota.cuotaBytes()) {
            throw new CuotaProyectoGradoExcedidaException(cuota);
        }
    }
}
