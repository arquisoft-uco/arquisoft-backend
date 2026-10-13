package com.arquisoft.solicitudes.domain.destinatario.model;

import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

import java.util.UUID;

public record ExistenciaDestinatario(UUID usuario, UsuarioDomain destinatario) {}
