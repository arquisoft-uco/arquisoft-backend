package com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.entity;

import java.time.LocalDate;
import java.util.UUID;

public record MapaRutaEntity(UUID id, UUID proyectoGrado, LocalDate fechaInicio, LocalDate fechaFin) {}
