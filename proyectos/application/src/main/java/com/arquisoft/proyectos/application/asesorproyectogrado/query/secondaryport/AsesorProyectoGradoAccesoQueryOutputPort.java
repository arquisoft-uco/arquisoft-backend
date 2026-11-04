package com.arquisoft.proyectos.application.asesorproyectogrado.query.secondaryport;

import java.util.UUID;

public interface AsesorProyectoGradoAccesoQueryOutputPort {

    boolean existeAsignacionVigente(UUID asesor, UUID proyectoGrado, String estadoAsesor);
}
