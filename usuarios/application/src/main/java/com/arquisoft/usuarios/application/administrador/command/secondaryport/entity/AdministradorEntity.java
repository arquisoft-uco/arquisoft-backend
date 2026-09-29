package com.arquisoft.usuarios.application.administrador.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record AdministradorEntity(UUID usuario, Instant eliminadoEn) {
}
