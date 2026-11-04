package com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.impl;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.VinculosMasRecientesProyectoGradoFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.mapper.AsesorProyectoGradoMapper;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VinculosMasRecientesProyectoGradoFinderImpl implements VinculosMasRecientesProyectoGradoFinder {

    private final AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @Override
    public List<AsesorProyectoGradoDomain> obtener(UUID proyectoGrado) {
        return asesorProyectoGradoOutputPort.obtenerMasRecientesPorProyectoGrado(proyectoGrado).stream()
                .map(AsesorProyectoGradoMapper::toDomain)
                .toList();
    }
}
