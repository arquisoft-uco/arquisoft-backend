package com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.impl;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.UltimoOcurridoEnProyectoGradoFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.shared.util.UtilFecha;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UltimoOcurridoEnProyectoGradoFinderImpl implements UltimoOcurridoEnProyectoGradoFinder {

    private final AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @Override
    public Instant obtener(UUID proyectoGrado) {
        return asesorProyectoGradoOutputPort
                .obtenerUltimoOcurridoEnPorProyectoGrado(proyectoGrado)
                .orElse(UtilFecha.VACIO);
    }
}
