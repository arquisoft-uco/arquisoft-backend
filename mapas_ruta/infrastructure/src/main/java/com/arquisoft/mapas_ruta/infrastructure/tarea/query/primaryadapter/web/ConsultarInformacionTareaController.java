package com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.interactor.ConsultarInformacionTareaInteractor;
import com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web.dto.TareaResponseDTO;
import com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web.mapper.ConsultarInformacionTareaRequestMapper;
import com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web.mapper.TareaResponseMapper;
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
import org.springframework.web.bind.annotation.GetMapping;
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
public class ConsultarInformacionTareaController {

    private final ConsultarInformacionTareaInteractor consultarInformacionTareaInteractor;

    @GetMapping("${rutas.mapas-ruta.tareas.por-id:/{tareaId}}")
    @Operation(
            summary = MapasRutaApiMessages.Tarea.CONSULTAR_INFORMACION_SUMMARY,
            description = MapasRutaApiMessages.Tarea.CONSULTAR_INFORMACION_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = MapasRutaApiMessages.Tarea.CONSULTAR_INFORMACION_RESP_200,
                    content = @Content(schema = @Schema(implementation = TareaResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = MapasRutaApiMessages.Tarea.CONSULTAR_INFORMACION_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = MapasRutaApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = MapasRutaApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.NOT_FOUND,
                    description = MapasRutaApiMessages.Tarea.CONSULTAR_INFORMACION_RESP_404)
    })
    public ResponseEntity<TareaResponseDTO> consultarInformacion(@PathVariable UUID tareaId) {
        return consultarInformacionTareaInteractor
                .ejecutar(ConsultarInformacionTareaRequestMapper.toQuery(tareaId))
                .map(TareaResponseMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
