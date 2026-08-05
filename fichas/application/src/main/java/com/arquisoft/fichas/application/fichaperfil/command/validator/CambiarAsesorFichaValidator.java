package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.fichaperfil.aggregate.CambiarAsesorFichaDomain;

import java.util.UUID;

public interface CambiarAsesorFichaValidator {

    void validar(CambiarAsesorFichaDomain cambio, UUID asesorFichaActual);
}
