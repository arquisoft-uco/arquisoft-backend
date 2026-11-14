package com.arquisoft.entregables.domain.paqueteentregable.model;

import java.util.UUID;

public record AccesoPaqueteEntregable(
        UUID entregableProyectoGrado,
        UUID actor,
        boolean comoAsesor,
        boolean comoEstudiante,
        UUID asesorVigente,
        boolean participa
) {}
