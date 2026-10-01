package com.arquisoft.usuarios.domain.administrador.model;

import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;

import java.util.UUID;

public record DisponibilidadAdministradorUsuario(UUID usuario, AdministradorDomain administrador) {}
