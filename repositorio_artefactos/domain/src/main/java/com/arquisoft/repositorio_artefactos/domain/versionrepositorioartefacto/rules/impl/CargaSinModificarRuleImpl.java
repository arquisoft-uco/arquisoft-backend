package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.CargaModificadaException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ConsistenciaCarga;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.CargaSinModificarRule;
import com.arquisoft.shared.util.UtilTexto;

public class CargaSinModificarRuleImpl implements CargaSinModificarRule {

    @Override
    public void validar(ConsistenciaCarga consistencia) {
        var sinEtag = UtilTexto.esVacioONulo(consistencia.etagConsultado())
                || UtilTexto.esVacioONulo(consistencia.etagAnalizado());
        if (sinEtag || !consistencia.etagConsultado().equals(consistencia.etagAnalizado())) {
            throw new CargaModificadaException(consistencia.carga());
        }
    }
}
