package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.CargaNoCorrespondeException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.CorrespondenciaCarga;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.CargaCorrespondeASolicitudRule;

public class CargaCorrespondeASolicitudRuleImpl implements CargaCorrespondeASolicitudRule {

    @Override
    public void validar(CorrespondenciaCarga correspondencia) {
        var carga = correspondencia.carga();
        if (!carga.getSolicitante().equals(correspondencia.solicitante())
                || !carga.getRepositorio().equals(correspondencia.repositorio())) {
            throw new CargaNoCorrespondeException(carga.getCarga());
        }
    }
}
