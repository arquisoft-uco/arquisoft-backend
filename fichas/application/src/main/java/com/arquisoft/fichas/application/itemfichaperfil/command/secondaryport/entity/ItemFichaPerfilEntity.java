package com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.entity;

import java.util.UUID;

public record ItemFichaPerfilEntity(UUID id, UUID fichaPerfilId, String tipoItem, String contenido) {
}
