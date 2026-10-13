package com.arquisoft.fichas.application.representantecomite.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record RepresentanteComiteEntity(UUID id, String identificador, String nombre, String email, Instant ocurridoEn,
                                        Instant eliminadoEn) {
}
