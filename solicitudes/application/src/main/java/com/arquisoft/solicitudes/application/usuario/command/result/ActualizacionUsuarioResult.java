package com.arquisoft.solicitudes.application.usuario.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface ActualizacionUsuarioResult {

    record Actualizada(UUID usuario) implements ActualizacionUsuarioResult {}

    record Descartada(UUID usuario, Instant ocurridoEnVigente) implements ActualizacionUsuarioResult {}

    record NoReplicado(UUID usuario) implements ActualizacionUsuarioResult {}
}
