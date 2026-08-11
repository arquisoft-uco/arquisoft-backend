package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoEvaluacionDuplicadoException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.secondaryport.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EstadoEvaluacionNoDuplicadoRule;

public class EstadoEvaluacionNoDuplicadoRuleImpl implements EstadoEvaluacionNoDuplicadoRule {

    private final EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;

    public EstadoEvaluacionNoDuplicadoRuleImpl(EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort) {
        this.estadoEvaluacionFichaOutputPort = estadoEvaluacionFichaOutputPort;
    }

    @Override
    public void validar(AgregacionEstadoEvaluacionFichaDomain entrada) {
        if (estadoEvaluacionFichaOutputPort.existePorEvaluacionYEstado(
                entrada.getEvaluacionFichaPerfil(), entrada.getEstadoEvaluacion().getId())) {
            throw new EstadoEvaluacionDuplicadoException(
                    entrada.getEvaluacionFichaPerfil(), entrada.getEstadoEvaluacion().getId());
        }
    }
}
