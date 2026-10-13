package com.arquisoft.usuarios.domain.usuario.model;

import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;

import java.util.UUID;

public record TransicionEstadoUsuario(UUID usuario, EstadoUsuario actual, EstadoUsuario destino) {}
