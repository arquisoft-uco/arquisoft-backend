package com.arquisoft.artefactos.domain.asesor.model;

import com.arquisoft.artefactos.domain.asesor.AsesorDomain;

import java.util.UUID;

public record ExistenciaAsesor(UUID asesor, AsesorDomain encontrado) {}
