package com.arquisoft.fichas.domain.estadofichaperfil.model;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;

import java.util.UUID;

public record TransicionEstadoFicha(UUID fichaPerfil, EstadoFicha estadoActual, EstadoFicha estadoNuevo) {}
