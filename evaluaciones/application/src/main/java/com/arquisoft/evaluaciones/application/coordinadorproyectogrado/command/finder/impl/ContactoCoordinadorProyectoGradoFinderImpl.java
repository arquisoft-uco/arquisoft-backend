package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.finder.impl;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.finder.ContactoCoordinadorProyectoGradoFinder;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.CoordinadorProyectoGradoOutputPort;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.mapper.CoordinadorProyectoGradoMapper;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.model.ContactoCoordinador;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContactoCoordinadorProyectoGradoFinderImpl implements ContactoCoordinadorProyectoGradoFinder {

    private final CoordinadorProyectoGradoOutputPort coordinadorProyectoGradoOutputPort;

    @Override
    public ContactoCoordinador obtener(UUID proyectoGrado) {
        return coordinadorProyectoGradoOutputPort.obtenerPorProyectoGrado(proyectoGrado)
                .map(CoordinadorProyectoGradoMapper::toContacto)
                .orElse(ContactoCoordinador.VACIO);
    }
}
