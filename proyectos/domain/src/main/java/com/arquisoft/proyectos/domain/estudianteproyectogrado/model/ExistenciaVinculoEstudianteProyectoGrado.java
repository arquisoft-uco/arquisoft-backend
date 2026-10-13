package com.arquisoft.proyectos.domain.estudianteproyectogrado.model;

import java.util.UUID;

public record ExistenciaVinculoEstudianteProyectoGrado(UUID proyectoGrado, UUID estudiante, boolean vinculado) {}
