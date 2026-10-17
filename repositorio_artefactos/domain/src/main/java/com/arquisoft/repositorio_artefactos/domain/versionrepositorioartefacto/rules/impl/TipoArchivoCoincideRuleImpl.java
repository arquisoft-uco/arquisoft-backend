package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.TipoArchivoNoCoincideException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.CoincidenciaTipoArchivo;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.TipoArchivoCoincideRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.TipoArchivoPlantilla;
import com.arquisoft.shared.util.UtilTexto;

public class TipoArchivoCoincideRuleImpl implements TipoArchivoCoincideRule {

    private static final char SEPARADOR_PARAMETROS = ';';

    @Override
    public void validar(CoincidenciaTipoArchivo coincidencia) {
        var plantilla = TipoArchivoPlantilla.desdeNombreArchivo(coincidencia.nombreArchivo());
        var tipoDetectado = tipoBase(coincidencia.analisis().getTipoDetectado());
        var coincide = !plantilla.esVacio()
                && plantilla.getTipoContenido().equals(coincidencia.tipoAlmacenado())
                && plantilla.getTipoDetectado().equals(tipoDetectado);
        if (!coincide) {
            throw new TipoArchivoNoCoincideException(coincidencia.tipoAlmacenado(), tipoDetectado);
        }
    }

    private static String tipoBase(String tipo) {
        var parametros = tipo.indexOf(SEPARADOR_PARAMETROS);
        return parametros < 0 ? tipo : UtilTexto.aplicarTrim(tipo.substring(0, parametros));
    }
}
