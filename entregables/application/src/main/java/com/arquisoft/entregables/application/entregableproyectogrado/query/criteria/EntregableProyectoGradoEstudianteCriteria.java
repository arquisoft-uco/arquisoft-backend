package com.arquisoft.entregables.application.entregableproyectogrado.query.criteria;

import java.util.UUID;

public record EntregableProyectoGradoEstudianteCriteria(
        UUID estudiante,
        UUID proyectoGrado,
        EntregableProyectoGradoCriteria consulta
) {}
