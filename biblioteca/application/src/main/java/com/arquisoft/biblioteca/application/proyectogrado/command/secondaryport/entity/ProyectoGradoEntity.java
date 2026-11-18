package com.arquisoft.biblioteca.application.proyectogrado.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record ProyectoGradoEntity(UUID id, String titulo, Instant ocurridoEn) {}
