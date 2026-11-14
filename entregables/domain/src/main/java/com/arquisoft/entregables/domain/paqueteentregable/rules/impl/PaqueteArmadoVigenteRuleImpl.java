package com.arquisoft.entregables.domain.paqueteentregable.rules.impl;

import com.arquisoft.entregables.domain.paqueteentregable.exception.PaqueteArmadoDesactualizadoException;
import com.arquisoft.entregables.domain.paqueteentregable.model.VigenciaPaqueteArmado;
import com.arquisoft.entregables.domain.paqueteentregable.rules.PaqueteArmadoVigenteRule;

public class PaqueteArmadoVigenteRuleImpl implements PaqueteArmadoVigenteRule {

    @Override
    public void validar(VigenciaPaqueteArmado vigencia) {
        if (vigencia.versionEmpaquetada() != vigencia.versionActual()
                || !vigencia.versionesEmpaquetadas().equals(vigencia.versionesActuales())) {
            throw new PaqueteArmadoDesactualizadoException(vigencia);
        }
    }
}
