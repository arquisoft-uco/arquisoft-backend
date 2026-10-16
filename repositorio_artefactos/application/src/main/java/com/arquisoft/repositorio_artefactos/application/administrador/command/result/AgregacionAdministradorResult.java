package com.arquisoft.repositorio_artefactos.application.administrador.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface AgregacionAdministradorResult {

    record Agregada(UUID administrador) implements AgregacionAdministradorResult {}

    record Reactivada(UUID administrador) implements AgregacionAdministradorResult {}

    record Duplicada(UUID administrador) implements AgregacionAdministradorResult {}

    record Descartada(UUID administrador, Instant ocurridoEnVigente) implements AgregacionAdministradorResult {}
}
