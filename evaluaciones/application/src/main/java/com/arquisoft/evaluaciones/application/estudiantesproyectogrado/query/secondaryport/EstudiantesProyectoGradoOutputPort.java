package com.arquisoft.evaluaciones.application.estudiantesproyectogrado.query.secondaryport;

import java.util.List;
import java.util.UUID;

public interface EstudiantesProyectoGradoOutputPort {

    List<UUID> obtenerEstudiantes(String proyecto);
}
