package com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.mapper;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper.TraspasarArtefactosMapper;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.model.AgregarAsesorProyectoGradoCommand;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AgregacionAsesorProyectoGradoDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;

public final class AgregarAsesorProyectoGradoMapper {

    private AgregarAsesorProyectoGradoMapper() {}

    public static AgregacionAsesorProyectoGradoDomain toDomain(AgregarAsesorProyectoGradoCommand command) {
        var vinculo = AsesorProyectoGradoDomain.crear(command.asesor(), command.proyectoGrado(), command.ocurridoEn());
        var traspaso = TraspasarArtefactosMapper.toDomain(vinculo.getProyectoGrado(), vinculo.getAsesor());
        return AgregacionAsesorProyectoGradoDomain.crear(vinculo, traspaso);
    }
}
