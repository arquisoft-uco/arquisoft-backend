package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.message.constant.ProyectosLimits;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.List;
import java.util.UUID;

public record RegistrarProyectoGradoCommand(
        UUID fichaPerfil,
        String tituloProyecto,
        UUID coordinador,
        List<UUID> estudiantes
) {

    public RegistrarProyectoGradoCommand {
        estudiantes = UtilColeccion.aplicarPorDefecto(estudiantes);
    }

    public static RegistrarProyectoGradoCommand crear(String fichaPerfil, String tituloProyecto, String coordinador,
                                                      List<String> estudiantes) {
        var result = new ValidationResult();
        var estudiantesTexto = UtilColeccion.aplicarPorDefecto(estudiantes);

        ValidatorUUID.uuidValido(fichaPerfil, ProyectosFields.ProyectoGrado.FICHA_PERFIL,
                ProyectosCodes.ProyectoGrado.FICHA_PERFIL_ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(tituloProyecto, ProyectosFields.ProyectoGrado.TITULO_PROYECTO,
                ProyectosCodes.ProyectoGrado.TITULO_REQUERIDO, result);
        ValidatorUUID.uuidValido(coordinador, ProyectosFields.ProyectoGrado.COORDINADOR,
                ProyectosCodes.ProyectoGrado.COORDINADOR_ID_REQUERIDO, result);
        if (ValidatorColeccion.noVacia(estudiantesTexto, ProyectosFields.EstudianteProyectoGrado.ESTUDIANTES,
                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTES_REQUERIDOS, result)) {
            ValidatorColeccion.tamanioMaximo(estudiantesTexto, ProyectosLimits.EstudianteProyectoGrado.MAX_ESTUDIANTES,
                    ProyectosFields.EstudianteProyectoGrado.ESTUDIANTES,
                    ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTES_MAXIMO, result);
            estudiantesTexto.forEach(estudiante -> ValidatorUUID.uuidValido(estudiante,
                    ProyectosFields.EstudianteProyectoGrado.ESTUDIANTES,
                    ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_ID_INVALIDO, result));
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(fichaPerfil),
                tituloProyecto,
                UtilUUID.generarUUIDDesdeTexto(coordinador),
                estudiantesTexto.stream().map(UtilUUID::generarUUIDDesdeTexto).toList());
    }
}
