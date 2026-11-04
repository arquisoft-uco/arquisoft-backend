package com.arquisoft.fichas.application.estadoficha.query.criteria;

import java.util.List;
import java.util.UUID;

public record EstadoFichaCriteria(
        UUID actor,
        List<String> rolesIdentidad
) {
}
