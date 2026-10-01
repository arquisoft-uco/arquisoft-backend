package com.arquisoft.solicitudes.application.usuario.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface AgregacionUsuarioResult {

    record Agregada(UUID usuario) implements AgregacionUsuarioResult {}

    record Duplicada(UUID usuario) implements AgregacionUsuarioResult {}

    record Descartada(UUID usuario, Instant ocurridoEnVigente) implements AgregacionUsuarioResult {}
}
