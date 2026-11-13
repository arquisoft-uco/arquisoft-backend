package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.dto;

import com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.dto.DocumentacionResponseDTO.BibliotecarioResponseDTO;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.dto.DocumentacionResponseDTO.EntregableProyectoGradoResponseDTO;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.dto.DocumentacionResponseDTO.EstadoDocumentacionResponseDTO;

import java.util.UUID;

public record EnlaceDocumentacionResponseDTO(
        String enlace,
        UUID id,
        EntregableProyectoGradoResponseDTO entregableProyectoGrado,
        BibliotecarioResponseDTO bibliotecario,
        EstadoDocumentacionResponseDTO estadoDocumentacion
) {
}
