package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.CambioVersionRepositorioNoPermitidoException;
import com.arquisoft.artefactos.domain.artefacto.model.MomentoCambioVersionRepositorio;
import com.arquisoft.artefactos.domain.artefacto.rules.CambioVersionRepositorioPermitidoRule;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;

public class CambioVersionRepositorioPermitidoRuleImpl implements CambioVersionRepositorioPermitidoRule {

    @Override
    public void validar(MomentoCambioVersionRepositorio momento) {
        var anterior = momento.versionAnterior();
        if (anterior.existeVersionAnterior()
                && !anterior.revision().tieneEstado(EstadoRevisionAsesor.PENDIENTE)
                && !anterior.revision().tieneEstado(EstadoRevisionAsesor.EN_PROGRESO)) {
            throw new CambioVersionRepositorioNoPermitidoException(
                    momento.documento(), momento.artefacto(), anterior.revision().getEstado().getId());
        }
    }
}
