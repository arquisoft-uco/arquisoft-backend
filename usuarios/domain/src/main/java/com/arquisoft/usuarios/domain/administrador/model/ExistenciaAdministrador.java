package com.arquisoft.usuarios.domain.administrador.model;

import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;

import java.util.UUID;

public record ExistenciaAdministrador(UUID usuario, AdministradorDomain administrador) {}
