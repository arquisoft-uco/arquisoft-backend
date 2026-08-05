package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilDomain;

import java.util.UUID;

public interface CambiarAsesorFichaValidator {

    void validar(FichaPerfilDomain ficha, UUID asesorFichaActual);
}
