package com.arquisoft.artefactos.application.paqueteentregable.command.result;

import java.util.UUID;

public sealed interface GeneracionPaqueteEntregableResult {

    record Generado(UUID paquete, UUID entregableProyectoGrado, long tamanoBytes)
            implements GeneracionPaqueteEntregableResult {}

    record Duplicado(UUID entregableProyectoGrado) implements GeneracionPaqueteEntregableResult {}

    record Omitido(UUID entregableProyectoGrado) implements GeneracionPaqueteEntregableResult {}
}
