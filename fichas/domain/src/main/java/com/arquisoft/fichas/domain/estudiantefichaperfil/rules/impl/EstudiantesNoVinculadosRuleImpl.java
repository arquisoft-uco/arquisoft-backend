package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculosEstudiantesFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantesNoVinculadosRule;
import com.arquisoft.shared.util.UtilCollection;

public class EstudiantesNoVinculadosRuleImpl implements EstudiantesNoVinculadosRule {

    @Override
    public void validar(VinculosEstudiantesFicha vinculos) {
        if (UtilCollection.isEmptyOrNull(vinculos.yaVinculados())) {
            return;
        }
        throw new EstudianteDuplicadoException(vinculos.yaVinculados().getFirst());
    }
}
