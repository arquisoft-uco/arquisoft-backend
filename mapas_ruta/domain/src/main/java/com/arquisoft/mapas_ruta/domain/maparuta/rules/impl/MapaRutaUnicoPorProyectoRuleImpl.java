package com.arquisoft.mapas_ruta.domain.maparuta.rules.impl;

import com.arquisoft.mapas_ruta.domain.maparuta.exception.MapaRutaDuplicadoException;
import com.arquisoft.mapas_ruta.domain.maparuta.model.DisponibilidadMapaRuta;
import com.arquisoft.mapas_ruta.domain.maparuta.rules.MapaRutaUnicoPorProyectoRule;

public class MapaRutaUnicoPorProyectoRuleImpl implements MapaRutaUnicoPorProyectoRule {

    @Override
    public void validar(DisponibilidadMapaRuta disponibilidad) {
        if (disponibilidad.mapaRutaExiste()) {
            throw new MapaRutaDuplicadoException(disponibilidad.proyectoGrado());
        }
    }
}
