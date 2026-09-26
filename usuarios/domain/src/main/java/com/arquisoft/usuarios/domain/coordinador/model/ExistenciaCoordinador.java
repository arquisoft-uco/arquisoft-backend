package com.arquisoft.usuarios.domain.coordinador.model;

import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;

import java.util.UUID;

public record ExistenciaCoordinador(UUID usuario, CoordinadorDomain coordinador) {}
