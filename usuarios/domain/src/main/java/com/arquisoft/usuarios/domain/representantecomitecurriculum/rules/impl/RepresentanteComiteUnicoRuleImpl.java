package com.arquisoft.usuarios.domain.representantecomitecurriculum.rules.impl;

import com.arquisoft.usuarios.domain.representantecomitecurriculum.exception.UsuarioYaEsRepresentanteException;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.model.DisponibilidadRepresentante;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.rules.RepresentanteComiteUnicoRule;

public final class RepresentanteComiteUnicoRuleImpl implements RepresentanteComiteUnicoRule {

    @Override
    public void validar(DisponibilidadRepresentante entrada) {
        if (entrada.yaEsRepresentante()) {
            throw new UsuarioYaEsRepresentanteException(entrada.usuario());
        }
    }
}
