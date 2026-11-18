package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.dto;

import java.time.Instant;

public record EnlaceDescargaDocumentacionResponseDTO(String url, Instant expiraEn) {
}
