package com.arquisoft.fichas.domain.estudiantefichaperfil.model;

import java.util.List;
import java.util.UUID;

public record VinculacionEstudiantesCriteria(UUID fichaPerfil, List<UUID> estudiantes) {}
