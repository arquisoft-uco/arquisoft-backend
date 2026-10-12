package com.arquisoft.usuarios.domain.administrador.rules.impl;

import com.arquisoft.usuarios.domain.administrador.exception.AdministradorNoEncontradoException;
import com.arquisoft.usuarios.domain.administrador.model.ExistenciaAdministrador;
import com.arquisoft.usuarios.domain.administrador.rules.AdministradorVigenteRule;

public class AdministradorVigenteRuleImpl implements AdministradorVigenteRule {

    @Override
    public void validar(ExistenciaAdministrador existencia) {
        var administrador = existencia.administrador();
        if (administrador.esVacio() || administrador.estaEliminado()) {
            throw new AdministradorNoEncontradoException(existencia.usuario());
        }
    }
}
