package com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record EstadoFichaPerfilEntity(
        UUID id, UUID fichaPerfilId, String estadoFicha, Instant fechaActualizacion) {
}
