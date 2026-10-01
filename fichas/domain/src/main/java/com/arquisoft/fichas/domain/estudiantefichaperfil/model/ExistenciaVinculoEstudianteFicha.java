package com.arquisoft.fichas.domain.estudiantefichaperfil.model;

import java.util.UUID;

public record ExistenciaVinculoEstudianteFicha(UUID fichaPerfil, UUID estudiante, boolean existe) {}
