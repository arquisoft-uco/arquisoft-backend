package com.arquisoft.proyectos.domain.proyectogrado.model;

import java.util.UUID;

public record PropiedadCoordinadorProyectoGrado(UUID proyectoGrado, UUID coordinadorEsperado,
                                                UUID coordinadorSolicitante) {}
