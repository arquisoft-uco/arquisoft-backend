package com.arquisoft.entregables.application.paqueteentregable.command.result;

import java.util.UUID;

public sealed interface AgregacionPaqueteEntregableResult {

    record Agregado(UUID entregableProyectoGrado) implements AgregacionPaqueteEntregableResult {}

    record Duplicado(UUID entregableProyectoGrado) implements AgregacionPaqueteEntregableResult {}
}
