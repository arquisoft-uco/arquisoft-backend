package com.arquisoft.biblioteca.application.documentacion.query.criteria;

import java.util.UUID;

public record DescargaDocumentacionCriteria(
        UUID documentacion,
        boolean comoBibliotecario
) {}
