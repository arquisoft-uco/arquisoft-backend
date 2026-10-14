package com.arquisoft.proyectos.application.asesorproyectogrado.query.secondaryport;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;

import java.util.List;
import java.util.UUID;

public interface AsesorProyectoGradoQueryOutputPort {

    List<AsesorProyectoGradoReadModel> consultarPorProyecto(UUID proyectoGrado);
}
