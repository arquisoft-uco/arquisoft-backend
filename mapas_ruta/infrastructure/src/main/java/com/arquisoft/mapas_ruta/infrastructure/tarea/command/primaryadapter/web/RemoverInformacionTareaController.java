package com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.interactor.RemoverInformacionTareaInteractor;
import com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.mapper.RemoverInformacionTareaRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.MapasRutaApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.mapas-ruta.tareas.base:/tareas}")
@RequiredArgsConstructor
@Tag(
        name = MapasRutaApiMessages.Tarea.TAG_NAME,
        description = MapasRutaApiMessages.Tarea.TAG_DESCRIPTION)
public class RemoverInformacionTareaController {

    private final RemoverInformacionTareaInteractor interactor;

    @DeleteMapping("${rutas.mapas-ruta.tareas.por-id:/{tareaId}}")
    @Operation(
            summary = MapasRutaApiMessages.Tarea.REMOVER_INFORMACION_SUMMARY,
            description = MapasRutaApiMessages.Tarea.REMOVER_INFORMACION_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = MapasRutaApiMessages.Tarea.REMOVER_INFORMACION_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = MapasRutaApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = MapasRutaApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = MapasRutaApiMessages.Tarea.REMOVER_INFORMACION_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> removerInformacion(@PathVariable UUID tareaId) {
        interactor.ejecutar(RemoverInformacionTareaRequestMapper.toCommand(tareaId));
        return ResponseEntity.noContent().build();
    }
}
