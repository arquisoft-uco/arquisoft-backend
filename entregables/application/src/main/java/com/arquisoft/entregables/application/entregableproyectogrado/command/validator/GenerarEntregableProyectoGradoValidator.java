package com.arquisoft.entregables.application.entregableproyectogrado.command.validator;

import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;
import com.arquisoft.entregables.domain.entregableproyectogrado.GeneracionEntregableProyectoGradoDomain;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;

import java.util.List;
import java.util.UUID;

public interface GenerarEntregableProyectoGradoValidator {

    void validar(GeneracionEntregableProyectoGradoDomain entrada, ProyectoGradoDomain proyecto,
                 boolean asesorVigente, UUID asesorVigenteProyecto, boolean existeVigente,
                 List<ArtefactoDomain> artefactosVigentes);
}
