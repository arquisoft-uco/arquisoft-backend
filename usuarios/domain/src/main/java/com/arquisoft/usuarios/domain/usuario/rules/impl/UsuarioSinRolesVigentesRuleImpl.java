package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
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
            vigentes.add(UsuariosRealmRoles.ESTUDIANTE);
        }
        if (vigente(roles.asesor().esVacio(), roles.asesor().estaEliminado())) {
            vigentes.add(UsuariosRealmRoles.ASESOR);
        }
        if (vigente(roles.asesorFicha().esVacio(), roles.asesorFicha().estaEliminado())) {
            vigentes.add(UsuariosRealmRoles.ASESOR_FICHA);
        }
        if (vigente(roles.coordinador().esVacio(), roles.coordinador().estaEliminado())) {
            vigentes.add(UsuariosRealmRoles.COORDINADOR);
        }
        if (vigente(roles.representanteComite().esVacio(), roles.representanteComite().estaEliminado())) {
            vigentes.add(UsuariosRealmRoles.REPRESENTANTE_COMITE);
        }
        // TODO HU251 (JURADO), HU241 (BIBLIOTECARIO), HU232 (ADMINISTRADOR):
        //  agregar el {Rol}Domain a RolesUsuario y marcarlo vigente aqui si existe y no esta eliminado,
        //  para que el usuario solo pueda eliminarse con ese rol ya removido.

        if (!vigentes.isEmpty()) {
            throw new UsuarioRolesVigentesException(roles.usuario(), String.join(SEPARADOR, vigentes));
        }
    }

    private boolean vigente(boolean esVacio, boolean estaEliminado) {
        return !esVacio && !estaEliminado;
    }
}
