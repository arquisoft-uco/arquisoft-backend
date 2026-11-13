package com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.primaryadapter.web.dto;

import java.util.UUID;

public record AutorDocumentacionResponseDTO(UUID id, String identificador, String nombre, String email) {
}
