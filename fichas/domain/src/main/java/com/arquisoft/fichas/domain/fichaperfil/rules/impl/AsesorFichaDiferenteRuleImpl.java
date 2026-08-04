package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.exception.MismoAsesorFichaException;
import com.arquisoft.fichas.domain.fichaperfil.model.CambioAsesorFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaDiferenteRule;

public class AsesorFichaDiferenteRuleImpl implements AsesorFichaDiferenteRule {

    @Override
    public void validar(CambioAsesorFichaCriteria criteria) {
        if (criteria.nuevoAsesorFicha().equals(criteria.asesorFichaActual())) {
            throw new MismoAsesorFichaException(criteria.asesorFichaActual());
        }
    }
}
