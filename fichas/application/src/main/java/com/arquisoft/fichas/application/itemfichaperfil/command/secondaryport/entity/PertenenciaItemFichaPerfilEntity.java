package com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record PertenenciaItemFichaPerfilEntity(
        UUID fichaPerfilId, boolean esPropietario,
        UUID estadoId, String estadoFicha, Instant fechaActualizacion) {
}
