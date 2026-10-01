package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web.dto;

import java.util.UUID;

public record ItemFichaPerfilResponseDTO(
        UUID id,
        UUID fichaPerfilId,
        String tipoItem,
        String tipoItemNombre,
        String contenido
) {
}
