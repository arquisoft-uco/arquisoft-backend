package com.arquisoft.artefactos.domain.versionartefacto.model;

import com.arquisoft.artefactos.domain.proyectogrado.ProyectoGradoDomain;

public record AlmacenamientoProyectoGrado(ProyectoGradoDomain proyectoGrado, long cuotaBytes, long usoBytes) {}
