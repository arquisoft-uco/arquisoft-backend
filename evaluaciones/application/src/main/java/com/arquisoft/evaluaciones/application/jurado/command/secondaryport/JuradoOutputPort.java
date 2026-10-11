package com.arquisoft.evaluaciones.application.jurado.command.secondaryport;

import java.util.Set;
import java.util.UUID;

public interface JuradoOutputPort {

    Set<UUID> consultarVigentesPorIds(Set<UUID> jurados);
}
