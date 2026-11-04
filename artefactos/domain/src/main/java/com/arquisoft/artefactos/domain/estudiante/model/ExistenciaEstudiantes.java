package com.arquisoft.artefactos.domain.estudiante.model;

import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;

import java.util.List;
import java.util.UUID;

public record ExistenciaEstudiantes(List<UUID> solicitados, List<EstudianteDomain> encontrados) {}
