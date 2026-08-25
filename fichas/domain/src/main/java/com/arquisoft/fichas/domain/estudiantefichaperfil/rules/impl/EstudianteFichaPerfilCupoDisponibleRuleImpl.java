package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.CupoEstudiantesExcedidoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.CupoEstudiantesFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudianteFichaPerfilCupoDisponibleRule;
import com.arquisoft.shared.message.constant.FichasLimits;

public class EstudianteFichaPerfilCupoDisponibleRuleImpl implements EstudianteFichaPerfilCupoDisponibleRule {

    @Override
    public void validar(CupoEstudiantesFicha cupo) {
        if (cupo.yaVinculados() + cupo.nuevos() > FichasLimits.FichaPerfil.ESTUDIANTES_MAX) {
            throw new CupoEstudiantesExcedidoException(FichasLimits.FichaPerfil.ESTUDIANTES_MAX);
        }
    }
}
