package com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.secondaryadapter.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ProyectoEstudianteAccesoId implements Serializable {

    private UUID proyecto;
    private UUID estudiante;

    public ProyectoEstudianteAccesoId() {}

    public ProyectoEstudianteAccesoId(UUID proyecto, UUID estudiante) {
        this.proyecto = proyecto;
        this.estudiante = estudiante;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProyectoEstudianteAccesoId that)) {
            return false;
        }
        return Objects.equals(proyecto, that.proyecto) && Objects.equals(estudiante, that.estudiante);
    }

    @Override
    public int hashCode() {
        return Objects.hash(proyecto, estudiante);
    }
}
