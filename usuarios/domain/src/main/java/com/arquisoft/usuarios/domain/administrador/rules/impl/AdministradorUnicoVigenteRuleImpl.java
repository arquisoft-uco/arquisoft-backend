package com.arquisoft.usuarios.domain.administrador.rules.impl;

import com.arquisoft.usuarios.domain.administrador.exception.AdministradorUnicoVigenteException;
import com.arquisoft.usuarios.domain.administrador.model.DisponibilidadRemocionAdministrador;
import com.arquisoft.usuarios.domain.administrador.rules.AdministradorUnicoVigenteRule;

public class AdministradorUnicoVigenteRuleImpl implements AdministradorUnicoVigenteRule {

    private static final long MINIMO_ADMINISTRADORES_VIGENTES = 1L;

    @Override
    public void validar(DisponibilidadRemocionAdministrador disponibilidad) {
        if (disponibilidad.administradoresVigentes() <= MINIMO_ADMINISTRADORES_VIGENTES) {
            throw new AdministradorUnicoVigenteException(disponibilidad.usuario());
        }
    }
}
