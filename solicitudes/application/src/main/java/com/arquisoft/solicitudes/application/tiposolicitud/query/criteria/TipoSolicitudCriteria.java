package com.arquisoft.solicitudes.application.tiposolicitud.query.criteria;

import java.util.List;
import java.util.UUID;

public record TipoSolicitudCriteria(UUID actor, List<String> rolesIdentidad) {
}
