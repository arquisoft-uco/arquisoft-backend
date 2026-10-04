package com.arquisoft.proyectos.application.proyectogrado.command.finder.impl;

import com.arquisoft.proyectos.application.proyectogrado.command.finder.ProyectoGradoDeFichaExisteFinder;
import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProyectoGradoDeFichaExisteFinderImpl implements ProyectoGradoDeFichaExisteFinder {

    private final ProyectoGradoOutputPort proyectoGradoOutputPort;

    @Override
    public Boolean obtener(UUID fichaPerfil) {
        return proyectoGradoOutputPort.existePorFichaPerfil(fichaPerfil);
    }
}
