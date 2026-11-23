package com.arquisoft.biblioteca.application.entregableproyectogrado.command.result;

import java.util.UUID;

public sealed interface AgregacionEntregableProyectoGradoResult {

    record Agregado(UUID entregableProyectoGrado, UUID proyectoGrado)
            implements AgregacionEntregableProyectoGradoResult {}

    record Descartado(UUID entregableProyectoGrado) implements AgregacionEntregableProyectoGradoResult {}
}
