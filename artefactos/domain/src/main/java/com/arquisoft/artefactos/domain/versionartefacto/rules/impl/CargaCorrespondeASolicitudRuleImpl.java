package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.CargaNoCorrespondeException;
import com.arquisoft.artefactos.domain.versionartefacto.model.CorrespondenciaCarga;
import com.arquisoft.artefactos.domain.versionartefacto.rules.CargaCorrespondeASolicitudRule;

public class CargaCorrespondeASolicitudRuleImpl implements CargaCorrespondeASolicitudRule {

    @Override
    public void validar(CorrespondenciaCarga correspondencia) {
        var carga = correspondencia.carga();
        if (!carga.getSolicitante().equals(correspondencia.solicitante())
                || !carga.getArtefacto().equals(correspondencia.artefacto())) {
            throw new CargaNoCorrespondeException(carga.getCarga(), carga.getNombreArchivo());
        }
    }
}
