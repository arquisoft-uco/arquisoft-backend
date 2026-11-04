package com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.interactor.AsignarTareaInteractor;
import com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.dto.AsignarTareaRequestDTO;
import com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.dto.AsignarTareaResponseDTO;
import com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.mapper.AsignarTareaRequestMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.mapas-ruta.mapas-ruta.base:/mapas-ruta}")
@RequiredArgsConstructor
@Tag(
        name = MapasRutaApiMessages.Tarea.TAG_NAME,
        description = MapasRutaApiMessages.Tarea.TAG_DESCRIPTION)
public class AsignarTareaController {

    private final AsignarTareaInteractor interactor;

    @PostMapping("${rutas.mapas-ruta.mapas-ruta.tareas:/{mapaRutaId}/tareas}")
    @Operation(
            summary = MapasRutaApiMessages.Tarea.ASIGNAR_SUMMARY,
            description = MapasRutaApiMessages.Tarea.ASIGNAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = MapasRutaApiMessages.Tarea.ASIGNAR_RESP_201,
                    content = @Content(schema = @Schema(implementation = AsignarTareaResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = MapasRutaApiMessages.Tarea.ASIGNAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = MapasRutaApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = MapasRutaApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = MapasRutaApiMessages.Tarea.ASIGNAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<AsignarTareaResponseDTO> asignar(
            @PathVariable UUID mapaRutaId,
            @RequestBody AsignarTareaRequestDTO request) {
        var id = interactor.ejecutar(AsignarTareaRequestMapper.toCommand(request, mapaRutaId));
        return ResponseEntity.status(HttpStatus.CREATED).body(new AsignarTareaResponseDTO(id));
    }
}
