package com.arquisoft.entregables.application.asesorproyectogrado.query.finder.impl;

import com.arquisoft.entregables.application.asesorproyectogrado.query.finder.AsesorVigenteProyectoGradoQueryFinder;
import com.arquisoft.entregables.application.asesorproyectogrado.query.secondaryport.AsesorProyectoGradoAccesoQueryOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorVigenteProyectoGradoQueryFinderImpl implements AsesorVigenteProyectoGradoQueryFinder {

    private final AsesorProyectoGradoAccesoQueryOutputPort asesorProyectoGradoAccesoQueryOutputPort;

    @Override
    public UUID obtener(UUID proyectoGrado) {
        return asesorProyectoGradoAccesoQueryOutputPort.obtenerAsesorVigentePorProyectoGrado(proyectoGrado)
                .orElse(UtilUUID.obtenerUUIDPorDefecto());
    }
}
