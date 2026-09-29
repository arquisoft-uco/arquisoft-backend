package com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface ProyectoGradoOutputPort {

    Optional<ProyectoGradoEntity> obtenerPorId(UUID proyectoGrado);
}
