package com.arquisoft.entregables.domain.paqueteentregable.model;

import java.util.Set;
import java.util.UUID;

public record VigenciaPaqueteArmado(
        UUID proyectoGrado,
        String nombreProyecto,
        int versionEmpaquetada,
        int versionActual,
        Set<UUID> versionesEmpaquetadas,
        Set<UUID> versionesActuales) {}
