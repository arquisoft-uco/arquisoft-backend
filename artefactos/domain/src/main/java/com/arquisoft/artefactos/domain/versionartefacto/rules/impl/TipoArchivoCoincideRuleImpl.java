package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.TipoArchivoNoCoincideException;
import com.arquisoft.artefactos.domain.versionartefacto.model.CoincidenciaTipoArchivo;
import com.arquisoft.artefactos.domain.versionartefacto.model.TipoArchivoVersionArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.rules.TipoArchivoCoincideRule;

public class TipoArchivoCoincideRuleImpl implements TipoArchivoCoincideRule {

    @Override
    public void validar(CoincidenciaTipoArchivo coincidencia) {
        var tipo = TipoArchivoVersionArtefacto.desdeNombreArchivo(coincidencia.nombreArchivo());
        var tipoDetectado = coincidencia.analisis().getTipoDetectado();
        var coincide = !tipo.esVacio()
                && tipo.getTipoContenido().equals(coincidencia.tipoAlmacenado())
                && tipo.getTiposDetectados().contains(tipoDetectado);
        if (!coincide) {
            throw new TipoArchivoNoCoincideException(coincidencia.nombreArchivo(), coincidencia.tipoAlmacenado(), tipoDetectado);
        }
    }
}
