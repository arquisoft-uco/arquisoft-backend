package com.arquisoft.mapas_ruta.domain.maparuta.rules.impl;

import com.arquisoft.mapas_ruta.domain.maparuta.exception.MapaRutaNoEncontradoException;
import com.arquisoft.mapas_ruta.domain.maparuta.model.ExistenciaMapaRuta;
import com.arquisoft.mapas_ruta.domain.maparuta.rules.MapaRutaExisteRule;

public class MapaRutaExisteRuleImpl implements MapaRutaExisteRule {

    @Override
    public void validar(ExistenciaMapaRuta existencia) {
        if (!existencia.existe()) {
            throw new MapaRutaNoEncontradoException(existencia.mapaRuta());
        }
    }
}
