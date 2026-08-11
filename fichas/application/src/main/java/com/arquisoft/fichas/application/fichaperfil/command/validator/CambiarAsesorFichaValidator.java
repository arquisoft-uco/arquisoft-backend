package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.fichaperfil.CambioAsesorFichaDomain;

import java.util.UUID;

public interface CambiarAsesorFichaValidator {

    void validar(CambioAsesorFichaDomain cambio, UUID asesorFichaActual);
}
