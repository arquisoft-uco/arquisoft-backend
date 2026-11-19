package com.arquisoft.shared.catalogoparametros.application.command.result;

public sealed interface RefrescoParametrosCatalogoResult {

    record Refrescado(int cantidad) implements RefrescoParametrosCatalogoResult {}

    record Conservado(String motivo) implements RefrescoParametrosCatalogoResult {}

    record Rechazado(int estado) implements RefrescoParametrosCatalogoResult {}

    record Suspendido() implements RefrescoParametrosCatalogoResult {}

    record SinConfigurar() implements RefrescoParametrosCatalogoResult {}
}
