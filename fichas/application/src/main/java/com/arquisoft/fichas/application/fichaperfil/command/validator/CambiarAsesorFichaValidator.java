package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;

import java.util.UUID;

public interface CambiarAsesorFichaValidator {

    void validar(FichaPerfilAggregate ficha, UUID asesorFichaActual);
}
