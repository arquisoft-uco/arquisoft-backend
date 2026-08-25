package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.domain.fichaperfil.model.DisponibilidadTituloFicha;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilTituloUnicoRule;

public class FichaPerfilTituloUnicoRuleImpl implements FichaPerfilTituloUnicoRule {

    @Override
    public void validar(DisponibilidadTituloFicha disponibilidad) {
        if (disponibilidad.yaExiste()) {
            throw new FichaTituloDuplicadoException(disponibilidad.tituloProyecto());
        }
    }
}
