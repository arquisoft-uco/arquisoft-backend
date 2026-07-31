package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.adapter.in.web.dto;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.validation.UuidValido;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record AsignarEstudiantesFichaPerfilRequestDTO(

        @NotEmpty(message = FichasMessages.EstudianteFichaPerfil.ESTUDIANTES_OBLIGATORIOS_MSG)
        @Size(max = FichasMessages.FichaPerfil.ESTUDIANTES_MAX,
                message = FichasMessages.EstudianteFichaPerfil.ESTUDIANTES_MAX_MSG)
        List<@UuidValido(message = FichasMessages.EstudianteFichaPerfil.ESTUDIANTE_FORMATO_UUID_MSG) String>
                estudiantes) {

    public AsignarEstudiantesFichaPerfilCommand toCommand(UUID fichaPerfil) {
        return new AsignarEstudiantesFichaPerfilCommand(
                fichaPerfil,
                estudiantes.stream().map(UtilUUID::generateUUIDFromString).toList());
    }
}
