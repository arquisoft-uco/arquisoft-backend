package com.arquisoft.usuarios.domain.estudiante.model;

import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;

import java.util.UUID;

public record ExistenciaEstudiante(UUID usuario, EstudianteDomain estudiante) {}
