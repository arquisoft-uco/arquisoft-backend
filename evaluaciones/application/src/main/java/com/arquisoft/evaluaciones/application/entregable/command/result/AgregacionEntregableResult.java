package com.arquisoft.evaluaciones.application.entregable.command.result;

import java.util.UUID;

public sealed interface AgregacionEntregableResult {

    record Agregado(UUID entregable) implements AgregacionEntregableResult {}

    record Duplicado(UUID entregable) implements AgregacionEntregableResult {}
}
