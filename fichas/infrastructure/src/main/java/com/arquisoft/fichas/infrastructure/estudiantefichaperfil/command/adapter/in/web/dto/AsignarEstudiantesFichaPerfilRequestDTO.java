package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.adapter.in.web.dto;

import com.arquisoft.shared.message.FichasLimits;
import com.arquisoft.shared.message.ValidationKeys;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.validation.UuidValido;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record AsignarEstudiantesFichaPerfilRequestDTO(

        @NotEmpty(message = ValidationKeys.EstudianteFichaPerfil.ESTUDIANTES_OBLIGATORIOS)
        @Size(max = FichasLimits.FichaPerfil.ESTUDIANTES_MAX,
                message = ValidationKeys.EstudianteFichaPerfil.ESTUDIANTES_MAXIMO)
        List<@UuidValido(message = ValidationKeys.EstudianteFichaPerfil.ESTUDIANTE_UUID) String>
                estudiantes) {

    public AsignarEstudiantesFichaPerfilCommand toCommand(UUID fichaPerfil) {
        return new AsignarEstudiantesFichaPerfilCommand(
                fichaPerfil,
                estudiantes.stream().map(UtilUUID::generateUUIDFromString).toList());
    }
}
