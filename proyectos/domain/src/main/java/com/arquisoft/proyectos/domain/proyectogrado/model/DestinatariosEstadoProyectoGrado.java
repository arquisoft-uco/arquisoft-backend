package com.arquisoft.proyectos.domain.proyectogrado.model;

import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ContactoAsesor;
import com.arquisoft.proyectos.domain.coordinador.model.ContactoCoordinador;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public record DestinatariosEstadoProyectoGrado(
        ContactoCoordinador coordinador,
        List<ContactoEstudiante> estudiantes,
        List<ContactoAsesor> asesores) {

    public DestinatariosEstadoProyectoGrado {
        estudiantes = UtilColeccion.aplicarPorDefecto(estudiantes);
        asesores = UtilColeccion.aplicarPorDefecto(asesores);
    }
}
