package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorColeccion;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AgregarEstudiantesProyectoGradoCommand(
        UUID proyectoGrado,
        String tituloProyecto,
        List<UUID> estudiantes,
        Instant ocurridoEn
) {
    public static AgregarEstudiantesProyectoGradoCommand crear(
            String proyectoGrado, String tituloProyecto, List<String> estudiantes, Instant ocurridoEn) {
        var result = new ValidationResult();
        var tituloRecortado = UtilTexto.aplicarTrim(tituloProyecto);

        if (ValidatorUUID.uuidValido(proyectoGrado, BibliotecaFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                BibliotecaCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)
                && UtilUUID.esPorDefecto(UtilUUID.generarUUIDDesdeTexto(proyectoGrado))) {
            result.agregarError(BibliotecaFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                    BibliotecaCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                    Mensajes.obtener(ValidadorKey.UUID_INVALIDO));
        }
        ValidatorTexto.noEnBlanco(tituloRecortado, BibliotecaFields.ProyectoGrado.TITULO,
                BibliotecaCodes.ProyectoGrado.TITULO_REQUERIDO, result);
        var lista = UtilColeccion.aplicarPorDefecto(estudiantes);
        if (ValidatorColeccion.noVacia(lista, BibliotecaFields.EstudianteProyectoGrado.AUTORES,
                BibliotecaCodes.EstudianteProyectoGrado.AUTORES_REQUERIDOS, result)) {
            lista.forEach(estudiante -> ValidatorUUID.uuidValido(estudiante,
                    BibliotecaFields.EstudianteProyectoGrado.AUTOR,
                    BibliotecaCodes.EstudianteProyectoGrado.AUTOR_REQUERIDO, result));
            ValidatorColeccion.sinDuplicados(lista, BibliotecaFields.EstudianteProyectoGrado.AUTORES,
                    BibliotecaCodes.EstudianteProyectoGrado.AUTOR_DUPLICADO, result);
        }
        ValidatorObjeto.noNulo(ocurridoEn, BibliotecaFields.EstudianteProyectoGrado.OCURRIDO_EN,
                BibliotecaCodes.EstudianteProyectoGrado.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarEstudiantesProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado),
                tituloRecortado,
                lista.stream().map(UtilUUID::generarUUIDDesdeTexto).toList(),
                ocurridoEn);
    }
}
