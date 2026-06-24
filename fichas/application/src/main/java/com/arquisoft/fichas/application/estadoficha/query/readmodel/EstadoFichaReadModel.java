package com.arquisoft.fichas.application.estadoficha.query.readmodel;

import java.util.UUID;

public record EstadoFichaReadModel(
        UUID id,
        String nombre,
        String descripcion
) {}
