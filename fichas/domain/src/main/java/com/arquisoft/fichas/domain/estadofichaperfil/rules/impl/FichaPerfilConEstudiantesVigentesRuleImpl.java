package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilSinEstudiantesVigentesException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.IntegrantesVigentesFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.FichaPerfilConEstudiantesVigentesRule;

public class FichaPerfilConEstudiantesVigentesRuleImpl implements FichaPerfilConEstudiantesVigentesRule {

    @Override
    public void validar(IntegrantesVigentesFicha integrantes) {
        if (integrantes.cantidad() == 0) {
            throw new FichaPerfilSinEstudiantesVigentesException(integrantes.fichaPerfil());
        }
    }
}
