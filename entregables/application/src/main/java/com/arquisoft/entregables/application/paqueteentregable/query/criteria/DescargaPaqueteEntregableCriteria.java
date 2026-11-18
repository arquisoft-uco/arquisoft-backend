package com.arquisoft.entregables.application.paqueteentregable.query.criteria;

import java.util.UUID;

public record DescargaPaqueteEntregableCriteria(
        UUID entregableProyectoGrado,
        UUID actor,
        boolean comoAsesor,
        boolean comoEstudiante
) {}
