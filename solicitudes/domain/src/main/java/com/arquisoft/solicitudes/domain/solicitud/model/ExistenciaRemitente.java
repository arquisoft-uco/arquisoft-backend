package com.arquisoft.solicitudes.domain.solicitud.model;

import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

import java.util.UUID;

public record ExistenciaRemitente(UUID usuario, UsuarioDomain remitente) {}
