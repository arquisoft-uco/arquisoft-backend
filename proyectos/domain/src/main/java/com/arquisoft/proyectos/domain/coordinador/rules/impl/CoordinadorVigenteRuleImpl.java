package com.arquisoft.proyectos.domain.coordinador.rules.impl;

import com.arquisoft.proyectos.domain.coordinador.exception.CoordinadorNoVigenteException;
import com.arquisoft.proyectos.domain.coordinador.model.VigenciaCoordinador;
import com.arquisoft.proyectos.domain.coordinador.rules.CoordinadorVigenteRule;

public class CoordinadorVigenteRuleImpl implements CoordinadorVigenteRule {

    @Override
    public void validar(VigenciaCoordinador vigencia) {
        var encontrado = vigencia.encontrado();
        if (encontrado.esVacio() || encontrado.estaEliminado()) {
            throw new CoordinadorNoVigenteException(vigencia.coordinador());
        }
    }
}
