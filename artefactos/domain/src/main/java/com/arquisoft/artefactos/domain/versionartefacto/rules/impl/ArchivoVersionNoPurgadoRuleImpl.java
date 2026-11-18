package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.ArchivoVersionPurgadoException;
import com.arquisoft.artefactos.domain.versionartefacto.model.DisponibilidadArchivoVersion;
import com.arquisoft.artefactos.domain.versionartefacto.rules.ArchivoVersionNoPurgadoRule;

public class ArchivoVersionNoPurgadoRuleImpl implements ArchivoVersionNoPurgadoRule {

    @Override
    public void validar(DisponibilidadArchivoVersion disponibilidad) {
        if (disponibilidad.proyectoFinalizado()) {
            throw new ArchivoVersionPurgadoException(disponibilidad);
        }
    }
}
