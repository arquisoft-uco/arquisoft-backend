package com.arquisoft.entregables.application.entregableproyectogrado.query.finder.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.finder.AsesorVigenteDeEntregableProyectoGradoQueryFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.EntregableProyectoGradoAccesoQueryOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorVigenteDeEntregableProyectoGradoQueryFinderImpl
        implements AsesorVigenteDeEntregableProyectoGradoQueryFinder {

    private final EntregableProyectoGradoAccesoQueryOutputPort entregableProyectoGradoAccesoQueryOutputPort;

    @Override
    public UUID obtener(UUID entregableProyectoGrado) {
        return entregableProyectoGradoAccesoQueryOutputPort.obtenerAsesorVigentePorId(entregableProyectoGrado)
                .orElse(UtilUUID.obtenerUUIDPorDefecto());
    }
}
