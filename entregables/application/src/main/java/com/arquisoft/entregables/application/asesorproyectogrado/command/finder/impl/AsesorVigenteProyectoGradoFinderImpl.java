package com.arquisoft.entregables.application.asesorproyectogrado.command.finder.impl;

import com.arquisoft.entregables.application.asesorproyectogrado.command.finder.AsesorVigenteProyectoGradoFinder;
import com.arquisoft.entregables.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorVigenteProyectoGradoFinderImpl implements AsesorVigenteProyectoGradoFinder {

    private final AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @Override
    public UUID obtener(UUID proyectoGrado) {
        return asesorProyectoGradoOutputPort.obtenerAsesorVigentePorProyectoGrado(proyectoGrado)
                .orElse(UtilUUID.obtenerUUIDPorDefecto());
    }
}
