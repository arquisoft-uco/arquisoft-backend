package com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.mapper.DesvincularEstudianteArtefactosMapper;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.model.DestituirEstudianteProyectoGradoCommand;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.DestitucionEstudianteProyectoGradoDomain;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;

public final class DestituirEstudianteProyectoGradoMapper {

    private DestituirEstudianteProyectoGradoMapper() {}

    public static DestitucionEstudianteProyectoGradoDomain toDomain(DestituirEstudianteProyectoGradoCommand command) {
        var vinculo = EstudianteProyectoGradoDomain.crear(
                command.estudiante(), command.proyectoGrado(), command.ocurridoEn());
        var desvinculacion = DesvincularEstudianteArtefactosMapper.toDomain(
                vinculo.getProyectoGrado(), vinculo.getEstudiante());
        return DestitucionEstudianteProyectoGradoDomain.crear(vinculo, desvinculacion);
    }
}
