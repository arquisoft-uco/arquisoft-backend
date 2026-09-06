package com.arquisoft.solicitudes.application.asignacionproyecto.command.secondaryport;

import java.util.UUID;

public interface AsignacionProyectoOutputPort {

    boolean esCoordinadorAsignado(UUID estudianteUsuario, UUID coordinadorUsuario);
}
