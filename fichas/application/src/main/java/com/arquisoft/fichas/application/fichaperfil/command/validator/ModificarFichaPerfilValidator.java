package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.ModificarFichaPerfilDomain;

public interface ModificarFichaPerfilValidator {

    void validar(ModificarFichaPerfilDomain modificacion, FichaPerfilDomain fichaActual);
}
