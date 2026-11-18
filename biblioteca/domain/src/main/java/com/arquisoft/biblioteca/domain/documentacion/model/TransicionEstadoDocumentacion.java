package com.arquisoft.biblioteca.domain.documentacion.model;

import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;

import java.util.UUID;

public record TransicionEstadoDocumentacion(
        UUID documentacion,
        EstadoDocumentacion actual,
        EstadoDocumentacion nuevo
) {}
