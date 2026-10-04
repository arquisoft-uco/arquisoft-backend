package com.arquisoft.usuarios.domain.representantecomite.rules.impl;

import com.arquisoft.usuarios.domain.representantecomite.exception.RepresentanteComiteUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.representantecomite.model.DisponibilidadRepresentanteComiteUsuario;
import com.arquisoft.usuarios.domain.representantecomite.rules.RepresentanteComiteUsuarioUnicoRule;

public class RepresentanteComiteUsuarioUnicoRuleImpl implements RepresentanteComiteUsuarioUnicoRule {

    @Override
    public void validar(DisponibilidadRepresentanteComiteUsuario disponibilidad) {
        var representanteComite = disponibilidad.representanteComite();
        if (!representanteComite.esVacio() && !representanteComite.estaEliminado()) {
            throw new RepresentanteComiteUsuarioDuplicadoException(disponibilidad.usuario());
        }
    }
}
