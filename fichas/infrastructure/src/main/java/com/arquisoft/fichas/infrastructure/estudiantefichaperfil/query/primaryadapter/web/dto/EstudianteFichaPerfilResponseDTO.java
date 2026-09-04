package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.primaryadapter.web.dto;

import java.util.UUID;

public record EstudianteFichaPerfilResponseDTO(
        UUID id,
        UUID fichaPerfilId,
        UUID estudianteId,
        String nombre,
        String email
) {
}
