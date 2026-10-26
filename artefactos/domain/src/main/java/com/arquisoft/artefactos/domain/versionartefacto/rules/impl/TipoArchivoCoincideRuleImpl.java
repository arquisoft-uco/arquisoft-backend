package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.TipoArchivoNoCoincideException;
import com.arquisoft.artefactos.domain.versionartefacto.model.CoincidenciaTipoArchivo;
import com.arquisoft.artefactos.domain.versionartefacto.model.TipoArchivoVersionArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.rules.TipoArchivoCoincideRule;
import com.arquisoft.shared.util.UtilTexto;

public class TipoArchivoCoincideRuleImpl implements TipoArchivoCoincideRule {

    private static final char SEPARADOR_PARAMETROS = ';';

    @Override
    public void validar(CoincidenciaTipoArchivo coincidencia) {
        var tipo = TipoArchivoVersionArtefacto.desdeNombreArchivo(coincidencia.nombreArchivo());
        var tipoDetectado = tipoBase(coincidencia.analisis().getTipoDetectado());
        var coincide = !tipo.esVacio()
                && tipo.getTipoContenido().equals(coincidencia.tipoAlmacenado())
                && tipo.getTiposDetectados().contains(tipoDetectado);
        if (!coincide) {
            throw new TipoArchivoNoCoincideException(coincidencia.tipoAlmacenado(), tipoDetectado);
        }
    }

    private static String tipoBase(String tipo) {
        var parametros = tipo.indexOf(SEPARADOR_PARAMETROS);
        return parametros < 0 ? tipo : UtilTexto.aplicarTrim(tipo.substring(0, parametros));
    }
}
