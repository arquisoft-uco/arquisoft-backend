package com.arquisoft.proyectos.domain.coordinador.model;

import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;

import java.util.UUID;

public record VigenciaCoordinador(UUID coordinador, CoordinadorDomain encontrado) {}
