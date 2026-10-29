package com.arquisoft.entregables.application.entregableproyectogrado.query.finder.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.finder.AsesorDeEntregableProyectoGradoQueryFinder;
import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.EntregableProyectoGradoAccesoQueryOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorDeEntregableProyectoGradoQueryFinderImpl implements AsesorDeEntregableProyectoGradoQueryFinder {

    private final EntregableProyectoGradoAccesoQueryOutputPort entregableProyectoGradoAccesoQueryOutputPort;

    @Override
    public UUID obtener(UUID entregableProyectoGrado) {
        return entregableProyectoGradoAccesoQueryOutputPort.obtenerAsesorPorId(entregableProyectoGrado)
                .orElse(UtilUUID.obtenerUUIDPorDefecto());
    }
}
