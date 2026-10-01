package com.arquisoft.solicitudes.domain.solicitud.model;

import java.util.UUID;

public record ExistenciaAsignacionResponsable(UUID estudianteUsuario, UUID responsableUsuario, boolean asignado) {}
