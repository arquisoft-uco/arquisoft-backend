package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.fichaperfil.CambioAsesorFichaDomain;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;

import java.util.Optional;

public interface CambiarAsesorFichaValidator {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    void validar(CambioAsesorFichaDomain cambio, Optional<FichaPerfilDomain> ficha, boolean asesorExiste,
                 Optional<EstadoFicha> estadoActual);
}
