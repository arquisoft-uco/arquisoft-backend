package com.arquisoft.mapas_ruta.application.asignacionasesor.command.secondaryport;

import java.util.UUID;

public interface AsignacionAsesorOutputPort {

    boolean estaAsignado(UUID proyectoGrado, UUID asesor);
}
