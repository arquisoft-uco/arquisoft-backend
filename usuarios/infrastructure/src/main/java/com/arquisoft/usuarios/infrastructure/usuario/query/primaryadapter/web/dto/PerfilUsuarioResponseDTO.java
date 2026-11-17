package com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web.dto;

import java.util.List;
import java.util.UUID;

public record PerfilUsuarioResponseDTO(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto,
        ElementoPerfilDTO estado,
        List<ElementoPerfilDTO> roles) {

    public record ElementoPerfilDTO(String id, String nombre) {
    }
}
