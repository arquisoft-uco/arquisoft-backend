package com.arquisoft.repositorio_artefactos.domain.administrador.model;

import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;

import java.util.UUID;

public record VigenciaAdministrador(UUID administrador, AdministradorDomain encontrado) {}
