package com.arquisoft.proyectos.application.proyectogrado.command.secondaryport;

import com.arquisoft.proyectos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface ProyectoGradoOutputPort {

    void registrar(ProyectoGradoEntity proyectoGrado);

    boolean existePorFichaPerfil(UUID fichaPerfil);

    Optional<ProyectoGradoEntity> obtenerPorId(UUID proyectoGrado);
}
