package com.arquisoft.fichas.domain.evaluacionfichaperfil.model;

import java.util.UUID;

public record DisponibilidadEvaluacionFicha(UUID representanteComite, UUID fichaPerfil, boolean yaExiste) {}
