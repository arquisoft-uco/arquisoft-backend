package com.arquisoft.fichas.domain.fichaperfil.model;

import java.util.UUID;

public record PropiedadAsesorFicha(UUID fichaPerfil, UUID asesorEsperado, UUID asesorSolicitante) {}
