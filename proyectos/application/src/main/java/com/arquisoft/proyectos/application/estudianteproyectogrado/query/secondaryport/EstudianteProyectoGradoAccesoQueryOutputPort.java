package com.arquisoft.proyectos.application.estudianteproyectogrado.query.secondaryport;

import java.util.UUID;

public interface EstudianteProyectoGradoAccesoQueryOutputPort {

    boolean existeVinculoVigente(UUID proyectoGrado, UUID estudiante);
}
