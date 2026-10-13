package com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.model;

import java.util.UUID;

public record DisponibilidadRepositorioArtefacto(
        String nombreRepositorio, String descripcion, UUID administrador, boolean yaExiste) {}
