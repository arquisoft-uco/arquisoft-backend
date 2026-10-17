package com.arquisoft.usuarios.domain.administrador.rules.impl;

import com.arquisoft.usuarios.domain.administrador.exception.AdministradorAutoeliminacionException;
import com.arquisoft.usuarios.domain.administrador.model.AutoeliminacionAdministrador;
import com.arquisoft.usuarios.domain.administrador.rules.AdministradorNoAutoeliminacionRule;

public class AdministradorNoAutoeliminacionRuleImpl implements AdministradorNoAutoeliminacionRule {

    @Override
    public void validar(AutoeliminacionAdministrador autoeliminacion) {
        if (autoeliminacion.actor().equals(autoeliminacion.usuario())) {
            throw new AdministradorAutoeliminacionException(autoeliminacion.usuario());
        }
    }
}
