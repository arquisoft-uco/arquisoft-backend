package com.arquisoft.repositorio_artefactos.application.administrador.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record AdministradorEntity(UUID id, String identificador, String nombre, String email, Instant ocurridoEn,
                           Instant eliminadoEn) {
}
