package com.arquisoft.fichas.domain.estudiante.model;

import java.util.List;
import java.util.UUID;

public record ExistenciaEstudiantes(List<UUID> solicitados, List<UUID> existentes) {}
