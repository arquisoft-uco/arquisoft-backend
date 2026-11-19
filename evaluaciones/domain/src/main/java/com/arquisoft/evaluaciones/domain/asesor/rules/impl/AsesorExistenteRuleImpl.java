package com.arquisoft.evaluaciones.domain.asesor.rules.impl;

import com.arquisoft.evaluaciones.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.evaluaciones.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.evaluaciones.domain.asesor.rules.AsesorExistenteRule;

public class AsesorExistenteRuleImpl implements AsesorExistenteRule {

    @Override
    public void validar(ExistenciaAsesor existencia) {
        if (!existencia.existe()) {
            throw new AsesorNoEncontradoException(existencia.asesor());
        }
    }
}
