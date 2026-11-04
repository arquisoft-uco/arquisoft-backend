package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.CargaModificadaException;
import com.arquisoft.artefactos.domain.versionartefacto.model.ConsistenciaCarga;
import com.arquisoft.artefactos.domain.versionartefacto.rules.CargaSinModificarRule;
import com.arquisoft.shared.util.UtilTexto;

public class CargaSinModificarRuleImpl implements CargaSinModificarRule {

    @Override
    public void validar(ConsistenciaCarga consistencia) {
        var sinEtag = UtilTexto.esVacioONulo(consistencia.etagConsultado())
                || UtilTexto.esVacioONulo(consistencia.etagAnalizado());
        if (sinEtag || !consistencia.etagConsultado().equals(consistencia.etagAnalizado())) {
            throw new CargaModificadaException(consistencia.carga(), consistencia.nombreArchivo());
        }
    }
}
