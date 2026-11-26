package com.arquisoft.shared.catalogoparametros.application.command.secondaryport.model;

import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.entity.ParametrosCatalogoEntity;

public sealed interface ObtencionParametrosCatalogo {

    record Obtenida(ParametrosCatalogoEntity entity) implements ObtencionParametrosCatalogo {}

    record NoDisponible(String motivo) implements ObtencionParametrosCatalogo {}

    record Rechazada(int estado) implements ObtencionParametrosCatalogo {}

    record SinConfigurar() implements ObtencionParametrosCatalogo {}
}
