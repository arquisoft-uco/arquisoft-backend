package com.arquisoft.entregables.application.asesorproyectogrado.command.finder.impl;

import com.arquisoft.entregables.application.asesorproyectogrado.command.finder.AsesorVigenteDeEntregableFinder;
import com.arquisoft.entregables.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorVigenteDeEntregableFinderImpl implements AsesorVigenteDeEntregableFinder {

    private final AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @Override
    public UUID obtener(UUID entregable) {
        return asesorProyectoGradoOutputPort.obtenerAsesorVigentePorEntregable(entregable)
                .orElse(UtilUUID.obtenerUUIDPorDefecto());
    }
}
