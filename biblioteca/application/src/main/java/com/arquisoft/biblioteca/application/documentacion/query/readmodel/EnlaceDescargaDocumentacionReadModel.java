package com.arquisoft.biblioteca.application.documentacion.query.readmodel;

import java.time.Instant;

public record EnlaceDescargaDocumentacionReadModel(String url, Instant expiraEn) {
}
