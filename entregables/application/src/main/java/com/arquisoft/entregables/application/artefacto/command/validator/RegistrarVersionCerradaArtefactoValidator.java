package com.arquisoft.entregables.application.artefacto.command.validator;

import com.arquisoft.entregables.domain.artefacto.RegistroVersionCerradaArtefactoDomain;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;

public interface RegistrarVersionCerradaArtefactoValidator {

    void validar(RegistroVersionCerradaArtefactoDomain registro, ProyectoGradoDomain proyecto);
}
