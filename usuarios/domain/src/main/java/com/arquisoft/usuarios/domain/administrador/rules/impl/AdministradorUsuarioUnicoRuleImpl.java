package com.arquisoft.usuarios.domain.administrador.rules.impl;

import com.arquisoft.usuarios.domain.administrador.exception.AdministradorUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.administrador.model.DisponibilidadAdministradorUsuario;
import com.arquisoft.usuarios.domain.administrador.rules.AdministradorUsuarioUnicoRule;

public class AdministradorUsuarioUnicoRuleImpl implements AdministradorUsuarioUnicoRule {

    @Override
    public void validar(DisponibilidadAdministradorUsuario disponibilidad) {
        var administrador = disponibilidad.administrador();
        if (!administrador.esVacio() && !administrador.estaEliminado()) {
            throw new AdministradorUsuarioDuplicadoException(disponibilidad.usuario());
        }
    }
}
