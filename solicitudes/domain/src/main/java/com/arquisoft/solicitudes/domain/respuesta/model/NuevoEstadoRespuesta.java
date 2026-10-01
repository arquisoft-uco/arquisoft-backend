package com.arquisoft.solicitudes.domain.respuesta.model;

import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;

import java.util.UUID;

public record NuevoEstadoRespuesta(UUID solicitud, EstadoRespuesta nuevoEstado) {}
