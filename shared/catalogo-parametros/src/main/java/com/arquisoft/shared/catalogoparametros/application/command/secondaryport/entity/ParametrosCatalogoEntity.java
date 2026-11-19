package com.arquisoft.shared.catalogoparametros.application.command.secondaryport.entity;

import com.arquisoft.shared.util.UtilObjeto;

import java.util.Map;

public record ParametrosCatalogoEntity(Map<String, String> valores) {

    public ParametrosCatalogoEntity {
        valores = Map.copyOf(UtilObjeto.aplicarPorDefecto(valores, Map.of()));
    }
}
