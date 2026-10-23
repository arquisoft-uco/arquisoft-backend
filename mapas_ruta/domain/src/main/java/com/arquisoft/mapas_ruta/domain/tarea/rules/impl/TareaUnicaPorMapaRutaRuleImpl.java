package com.arquisoft.mapas_ruta.domain.tarea.rules.impl;

import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaDuplicadaException;
import com.arquisoft.mapas_ruta.domain.tarea.model.DisponibilidadTarea;
import com.arquisoft.mapas_ruta.domain.tarea.rules.TareaUnicaPorMapaRutaRule;

public class TareaUnicaPorMapaRutaRuleImpl implements TareaUnicaPorMapaRutaRule {

    @Override
    public void validar(DisponibilidadTarea disponibilidad) {
        if (disponibilidad.existe()) {
            throw new TareaDuplicadaException(disponibilidad.descripcion(), disponibilidad.mapaRuta());
        }
    }
}
