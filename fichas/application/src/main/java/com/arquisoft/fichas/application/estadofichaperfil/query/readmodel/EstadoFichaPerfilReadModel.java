package com.arquisoft.fichas.application.estadofichaperfil.query.readmodel;

import java.time.Instant;

public record EstadoFichaPerfilReadModel(
        String id,
        String nombre,
        Instant fechaActualizacion
) {
}
