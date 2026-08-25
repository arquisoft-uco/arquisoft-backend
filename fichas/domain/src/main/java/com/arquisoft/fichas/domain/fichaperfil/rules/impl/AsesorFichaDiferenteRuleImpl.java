package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.exception.MismoAsesorFichaException;
import com.arquisoft.fichas.domain.fichaperfil.model.AsesorFichaComparacion;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaDiferenteRule;

public class AsesorFichaDiferenteRuleImpl implements AsesorFichaDiferenteRule {

    @Override
    public void validar(AsesorFichaComparacion comparacion) {
        if (comparacion.nuevoAsesorFicha().equals(comparacion.asesorFichaActual())) {
            throw new MismoAsesorFichaException(comparacion.asesorFichaActual());
        }
    }
}
