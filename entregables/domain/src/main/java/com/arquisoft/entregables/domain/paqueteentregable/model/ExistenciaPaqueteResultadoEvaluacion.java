package com.arquisoft.entregables.domain.paqueteentregable.model;

import java.util.UUID;

public record ExistenciaPaqueteResultadoEvaluacion(UUID entregableProyectoGrado, boolean requerido, boolean existe) {}
