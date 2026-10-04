package com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.message.constant.ProyectosLimits;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.List;
import java.util.UUID;

public record AsignarEstudiantesProyectoGradoCommand(
        UUID proyectoGrado,
        List<UUID> estudiantes
) {

    public AsignarEstudiantesProyectoGradoCommand {
        estudiantes = UtilColeccion.aplicarPorDefecto(estudiantes);
    }

    public static AsignarEstudiantesProyectoGradoCommand crear(UUID proyectoGrado, List<String> estudiantes) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.EstudianteProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result);

        var lista = UtilColeccion.aplicarPorDefecto(estudiantes);
        if (ValidatorColeccion.noVacia(lista,
                ProyectosFields.EstudianteProyectoGrado.ESTUDIANTES,
                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTES_REQUERIDOS, result)) {
            ValidatorColeccion.tamanioMaximo(lista, ProyectosLimits.EstudianteProyectoGrado.MAX_ESTUDIANTES,
                    ProyectosFields.EstudianteProyectoGrado.ESTUDIANTES,
                    ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTES_MAXIMO, result);
            lista.forEach(estudiante -> ValidatorUUID.uuidValido(estudiante,
                    ProyectosFields.EstudianteProyectoGrado.ESTUDIANTES,
                    ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_ID_INVALIDO, result));
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new AsignarEstudiantesProyectoGradoCommand(
                proyectoGrado, lista.stream().map(UtilUUID::generarUUIDDesdeTexto).toList());
    }
}
