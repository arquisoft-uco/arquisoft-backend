package com.arquisoft.solicitudes.domain.solicitud.model;

import java.util.UUID;

public record TipoSolicitudConcordante(UUID solicitud, String tipoActual, String tipoEsperado) {}
