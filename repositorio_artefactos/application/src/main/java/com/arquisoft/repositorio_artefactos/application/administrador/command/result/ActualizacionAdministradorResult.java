package com.arquisoft.repositorio_artefactos.application.administrador.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface ActualizacionAdministradorResult {

    record Actualizada(UUID administrador) implements ActualizacionAdministradorResult {}

    record Descartada(UUID administrador, Instant ocurridoEnVigente) implements ActualizacionAdministradorResult {}

    record NoReplicado(UUID administrador) implements ActualizacionAdministradorResult {}
}
