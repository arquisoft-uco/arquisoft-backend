package com.arquisoft.usuarios.domain.coordinador.model;

import java.util.UUID;

public record DisponibilidadCoordinadorUsuario(UUID usuario, boolean yaEsCoordinador) {}
