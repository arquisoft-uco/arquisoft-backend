package com.arquisoft.entregables.application.paqueteentregable.command.primaryport.mapper;

import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model.AgregarPaqueteEntregableCommand;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;

public final class AgregarPaqueteEntregableMapper {

    private AgregarPaqueteEntregableMapper() {}

    public static PaqueteEntregableDomain toDomain(AgregarPaqueteEntregableCommand command) {
        return PaqueteEntregableDomain.crear(
                command.paqueteEntregable(),
                command.entregableProyectoGrado(),
                command.bucket(),
                command.claveObjeto(),
                command.nombreArchivo(),
                command.tamanoBytes(),
                command.generadoEn());
    }
}
