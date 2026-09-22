package com.arquisoft.usuarios.domain.usuario.model;

import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

import java.util.UUID;

public record ExistenciaUsuario(UUID usuario, UsuarioDomain encontrado) {}
