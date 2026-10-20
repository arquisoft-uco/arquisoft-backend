package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioRolesVigentesException;
import com.arquisoft.usuarios.domain.usuario.model.RolesUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioSinRolesVigentesRule;

import java.util.ArrayList;

public class UsuarioSinRolesVigentesRuleImpl implements UsuarioSinRolesVigentesRule {

    private static final String SEPARADOR = ", ";

    @Override
    public void validar(RolesUsuario roles) {
        var vigentes = new ArrayList<String>();

        if (vigente(roles.estudiante().esVacio(), roles.estudiante().estaEliminado())) {
            vigentes.add(UsuariosRoles.ESTUDIANTE);
        }
        if (vigente(roles.asesor().esVacio(), roles.asesor().estaEliminado())) {
            vigentes.add(UsuariosRoles.ASESOR);
        }
        if (vigente(roles.asesorFicha().esVacio(), roles.asesorFicha().estaEliminado())) {
            vigentes.add(UsuariosRoles.ASESOR_FICHA);
        }
        if (vigente(roles.coordinador().esVacio(), roles.coordinador().estaEliminado())) {
            vigentes.add(UsuariosRoles.COORDINADOR);
        }
        if (vigente(roles.representanteComite().esVacio(), roles.representanteComite().estaEliminado())) {
            vigentes.add(UsuariosRoles.REPRESENTANTE_COMITE);
        }
        if (vigente(roles.administrador().esVacio(), roles.administrador().estaEliminado())) {
            vigentes.add(UsuariosRoles.ADMINISTRADOR);
        }
        if (vigente(roles.bibliotecario().esVacio(), roles.bibliotecario().estaEliminado())) {
            vigentes.add(UsuariosRoles.BIBLIOTECARIO);
        }
        if (vigente(roles.jurado().esVacio(), roles.jurado().estaEliminado())) {
            vigentes.add(UsuariosRoles.JURADO);
        }

        if (!vigentes.isEmpty()) {
            throw new UsuarioRolesVigentesException(roles.usuario(), String.join(SEPARADOR, vigentes));
        }
    }

    private boolean vigente(boolean esVacio, boolean estaEliminado) {
        return !esVacio && !estaEliminado;
    }
}
