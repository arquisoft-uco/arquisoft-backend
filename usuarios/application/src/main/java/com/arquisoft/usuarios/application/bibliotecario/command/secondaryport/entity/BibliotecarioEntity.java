package com.arquisoft.usuarios.application.bibliotecario.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record BibliotecarioEntity(UUID usuario, Instant eliminadoEn) {
}
