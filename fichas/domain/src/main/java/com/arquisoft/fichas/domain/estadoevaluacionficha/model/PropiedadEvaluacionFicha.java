package com.arquisoft.fichas.domain.estadoevaluacionficha.model;

import java.util.UUID;

public record PropiedadEvaluacionFicha(UUID evaluacionFichaPerfil, UUID representanteComite, boolean esPropietario) {}
