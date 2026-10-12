package com.arquisoft.proyectos.application.proyectogrado.command.validator;

import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;

import java.util.UUID;

public interface RegistrarProyectoGradoValidator {

    void validar(UUID coordinador, CoordinadorDomain coordinadorEncontrado);
}
