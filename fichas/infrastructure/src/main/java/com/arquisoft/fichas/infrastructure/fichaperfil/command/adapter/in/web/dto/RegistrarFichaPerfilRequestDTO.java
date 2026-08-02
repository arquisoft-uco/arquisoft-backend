package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto;

import com.arquisoft.shared.message.FichasLimits;
import com.arquisoft.shared.message.ValidationKeys;
import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.validation.UuidValido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RegistrarFichaPerfilRequestDTO(

        @NotBlank(message = ValidationKeys.FichaPerfil.TITULO_OBLIGATORIO)
        @Size(max = FichasLimits.FichaPerfil.TITULO_MAX,
                message = ValidationKeys.FichaPerfil.TITULO_MAXIMO)
        String tituloProyecto,

        @NotBlank(message = ValidationKeys.FichaPerfil.ASESOR_OBLIGATORIO)
        @UuidValido(message = ValidationKeys.FichaPerfil.ASESOR_UUID)
        String asesorFicha,

        @Size(max = FichasLimits.FichaPerfil.ESTUDIANTES_MAX,
                message = ValidationKeys.FichaPerfil.ESTUDIANTES_MAXIMO)
        List<@UuidValido(message = ValidationKeys.FichaPerfil.ESTUDIANTE_UUID) String> estudiantes) {

    public RegistrarFichaPerfilCommand toCommand() {
        return new RegistrarFichaPerfilCommand(
                tituloProyecto,
                UtilUUID.generateUUIDFromString(asesorFicha),
                estudiantes == null
                        ? null
                        : estudiantes.stream().map(UtilUUID::generateUUIDFromString).toList());
    }
}
