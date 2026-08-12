package com.arquisoft.fichas.domain.estudiantefichaperfil.model;

import java.util.UUID;

public record PropiedadFicha(UUID fichaPerfil, UUID estudiante, boolean esPropietario) {}
