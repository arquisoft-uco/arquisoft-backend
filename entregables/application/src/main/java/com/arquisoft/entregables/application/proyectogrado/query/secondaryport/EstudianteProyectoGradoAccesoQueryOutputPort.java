package com.arquisoft.entregables.application.proyectogrado.query.secondaryport;

import java.util.UUID;

public interface EstudianteProyectoGradoAccesoQueryOutputPort {

    boolean perteneceAlProyecto(UUID estudiante, UUID proyectoGrado);
}
