package com.arquisoft.usuarios.domain.estudiante.model;

import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;

import java.util.UUID;

public record DisponibilidadEstudianteUsuario(UUID usuario, String nombre, EstudianteDomain estudiante) {}
