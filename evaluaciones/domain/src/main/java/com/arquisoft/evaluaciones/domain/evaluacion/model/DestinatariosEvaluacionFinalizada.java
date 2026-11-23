package com.arquisoft.evaluaciones.domain.evaluacion.model;

import com.arquisoft.evaluaciones.domain.asesor.model.ContactoAsesor;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.model.ContactoCoordinador;
import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilTexto;

import java.util.List;
import java.util.UUID;

public record DestinatariosEvaluacionFinalizada(
        UUID evaluacion,
        ContactoCoordinador coordinador,
        List<ContactoEstudiante> estudiantes,
        ContactoAsesor asesor) {

    public DestinatariosEvaluacionFinalizada {
        estudiantes = UtilColeccion.aplicarPorDefecto(estudiantes);
    }

    public List<ContactoEstudiante> estudiantesConCorreo() {
        return estudiantes.stream()
                .filter(estudiante -> !UtilTexto.esVacioONulo(estudiante.email()))
                .toList();
    }
}
