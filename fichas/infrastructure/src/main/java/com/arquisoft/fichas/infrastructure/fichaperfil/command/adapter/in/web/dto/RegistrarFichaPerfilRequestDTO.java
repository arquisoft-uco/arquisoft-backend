package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto;

import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.validation.UuidValido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO pass-through: transporta los datos de entrada y garantiza su integridad
 * (obligatoriedad, longitud, formato) con mensajes del catálogo; las reglas de
 * negocio pertenecen al dominio. Los identificadores viajan como {@code String}
 * y se convierten a {@code UUID} en {@link #toCommand()} tras validar formato.
 */
public record RegistrarFichaPerfilRequestDTO(

        @NotBlank(message = FichasMessages.FichaPerfil.TITULO_OBLIGATORIO_MSG)
        @Size(max = FichasMessages.FichaPerfil.TITULO_MAX,
                message = FichasMessages.FichaPerfil.TITULO_MAX_MSG)
        String tituloProyecto,

        @NotBlank(message = FichasMessages.FichaPerfil.ASESOR_OBLIGATORIO_MSG)
        @UuidValido(message = FichasMessages.FichaPerfil.ASESOR_FORMATO_UUID_MSG)
        String asesorFicha,

        @Size(max = FichasMessages.FichaPerfil.ESTUDIANTES_MAX,
                message = FichasMessages.FichaPerfil.ESTUDIANTES_MAX_MSG)
        List<@UuidValido(message = FichasMessages.FichaPerfil.ESTUDIANTE_FORMATO_UUID_MSG) String> estudiantes) {

    public RegistrarFichaPerfilCommand toCommand() {
        return new RegistrarFichaPerfilCommand(
                tituloProyecto,
                UtilUUID.generateUUIDFromString(asesorFicha),
                estudiantes == null
                        ? null
                        : estudiantes.stream().map(UtilUUID::generateUUIDFromString).toList());
    }
}
