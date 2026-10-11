package com.arquisoft.solicitudes.domain.solicitud.model;

import java.util.UUID;

public record PropiedadDestinatarioSolicitud(UUID solicitud, UUID destinatarioUsuario, UUID solicitante) {}
