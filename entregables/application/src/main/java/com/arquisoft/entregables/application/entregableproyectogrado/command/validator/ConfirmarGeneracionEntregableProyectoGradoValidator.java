package com.arquisoft.entregables.application.entregableproyectogrado.command.validator;

import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;

import java.util.Set;
import java.util.UUID;

public interface ConfirmarGeneracionEntregableProyectoGradoValidator {

    void validar(ProyectoGradoDomain proyecto, int versionEmpaquetada, int versionActual,
                 Set<UUID> versionesEmpaquetadas, Set<UUID> versionesActuales);
}
