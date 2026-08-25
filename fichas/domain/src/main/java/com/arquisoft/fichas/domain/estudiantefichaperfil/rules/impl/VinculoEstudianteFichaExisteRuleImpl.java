package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ExistenciaVinculoEstudianteFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.VinculoEstudianteFichaExisteRule;

public class VinculoEstudianteFichaExisteRuleImpl implements VinculoEstudianteFichaExisteRule {

    @Override
    public void validar(ExistenciaVinculoEstudianteFicha existencia) {
        if (!existencia.existe()) {
            throw new EstudianteFichaPerfilNoEncontradoException(
                    existencia.estudiante(), existencia.fichaPerfil());
        }
    }
}
