package com.arquisoft.fichas.application.itemfichaperfil.command.model;

import java.util.UUID;

public record RemoverItemFichaPerfilCommand(UUID itemId, UUID estudianteId) {
}
