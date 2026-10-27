package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.TipoArchivoNoCoincideException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.CoincidenciaTipoArchivo;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.TipoArchivoCoincideRule;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.TipoArchivoPlantilla;

public class TipoArchivoCoincideRuleImpl implements TipoArchivoCoincideRule {

    @Override
    public void validar(CoincidenciaTipoArchivo coincidencia) {
        var plantilla = TipoArchivoPlantilla.desdeNombreArchivo(coincidencia.nombreArchivo());
        var tipoDetectado = coincidencia.analisis().getTipoDetectado();
        var coincide = !plantilla.esVacio()
                && plantilla.getTipoContenido().equals(coincidencia.tipoAlmacenado())
                && plantilla.getTipoDetectado().equals(tipoDetectado);
        if (!coincide) {
            throw new TipoArchivoNoCoincideException(coincidencia.tipoAlmacenado(), tipoDetectado);
        }
    }
}
