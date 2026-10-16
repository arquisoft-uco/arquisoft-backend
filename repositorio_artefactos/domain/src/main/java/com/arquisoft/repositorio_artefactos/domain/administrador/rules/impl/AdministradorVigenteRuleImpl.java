package com.arquisoft.repositorio_artefactos.domain.administrador.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.administrador.exception.AdministradorNoVigenteException;
import com.arquisoft.repositorio_artefactos.domain.administrador.model.VigenciaAdministrador;
import com.arquisoft.repositorio_artefactos.domain.administrador.rules.AdministradorVigenteRule;

public class AdministradorVigenteRuleImpl implements AdministradorVigenteRule {

    @Override
    public void validar(VigenciaAdministrador vigencia) {
        var encontrado = vigencia.encontrado();
        if (encontrado.esVacio() || encontrado.estaEliminado()) {
            throw new AdministradorNoVigenteException(vigencia.administrador());
        }
    }
}
