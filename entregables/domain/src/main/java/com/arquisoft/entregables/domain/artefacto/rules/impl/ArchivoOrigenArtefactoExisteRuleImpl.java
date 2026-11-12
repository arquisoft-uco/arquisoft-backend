package com.arquisoft.entregables.domain.artefacto.rules.impl;

import com.arquisoft.entregables.domain.artefacto.exception.ArchivoOrigenArtefactoNoDisponibleException;
import com.arquisoft.entregables.domain.artefacto.model.ExistenciaArchivoOrigenArtefacto;
import com.arquisoft.entregables.domain.artefacto.rules.ArchivoOrigenArtefactoExisteRule;

public class ArchivoOrigenArtefactoExisteRuleImpl implements ArchivoOrigenArtefactoExisteRule {

    @Override
    public void validar(ExistenciaArchivoOrigenArtefacto entrada) {
        if (!entrada.existe()) {
            throw new ArchivoOrigenArtefactoNoDisponibleException(
                    entrada.artefacto(), entrada.nombreDocumento(), entrada.claveOrigen());
        }
    }
}
