package com.arquisoft.biblioteca.application.bibliotecario.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record BibliotecarioEntity(UUID id, String identificador, String nombre, String email, Instant ocurridoEn,
                           Instant eliminadoEn) {
}
