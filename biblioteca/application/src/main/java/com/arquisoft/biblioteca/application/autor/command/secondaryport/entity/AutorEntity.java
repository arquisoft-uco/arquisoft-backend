package com.arquisoft.biblioteca.application.autor.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record AutorEntity(UUID id, String identificador, String nombre, String email, Instant ocurridoEn,
                           Instant eliminadoEn) {
}
