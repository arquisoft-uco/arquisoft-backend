package com.arquisoft.entregables.application.paqueteentregable.command.primaryport.mapper;

import com.arquisoft.entregables.application.artefactoentregable.command.primaryport.mapper.AgregarArtefactosEntregableMapper;
import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model.AgregarPaqueteEntregableCommand;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.entregables.domain.paqueteentregable.RegistroPaqueteEntregableDomain;

public final class AgregarPaqueteEntregableMapper {

    private AgregarPaqueteEntregableMapper() {}

    public static RegistroPaqueteEntregableDomain toDomain(AgregarPaqueteEntregableCommand command) {
        var paquete = PaqueteEntregableDomain.crear(
                command.paqueteEntregable(),
                command.entregableProyectoGrado(),
                command.bucket(),
                command.claveObjeto(),
                command.nombreArchivo(),
                command.tamanoBytes(),
                command.generadoEn());
        var artefactos = AgregarArtefactosEntregableMapper.toDomain(
                paquete.getEntregableProyectoGrado(), command.artefactos());
        return RegistroPaqueteEntregableDomain.crear(paquete, artefactos);
    }
}
