package com.arquisoft.entregables.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.CancelarProyectoGradoCommand;
import com.arquisoft.entregables.domain.artefacto.PurgaArtefactosProyectoGradoDomain;
import com.arquisoft.entregables.domain.paqueteentregable.PurgaPaquetesProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.CancelacionProyectoGradoDomain;

public final class CancelarProyectoGradoMapper {

    private CancelarProyectoGradoMapper() {}

    public static CancelacionProyectoGradoDomain toDomain(CancelarProyectoGradoCommand command) {
        var purgaPaquetes = PurgaPaquetesProyectoGradoDomain.crear(command.proyectoGrado());
        var purgaArtefactos = PurgaArtefactosProyectoGradoDomain.crear(command.proyectoGrado());
        return CancelacionProyectoGradoDomain.crear(command.proyectoGrado(), purgaPaquetes, purgaArtefactos);
    }
}
