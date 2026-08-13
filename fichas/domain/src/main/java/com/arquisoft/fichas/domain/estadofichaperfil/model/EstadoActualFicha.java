package com.arquisoft.fichas.domain.estadofichaperfil.model;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;

import java.util.UUID;

public record EstadoActualFicha(UUID fichaPerfil, EstadoFicha estadoActual) {}
