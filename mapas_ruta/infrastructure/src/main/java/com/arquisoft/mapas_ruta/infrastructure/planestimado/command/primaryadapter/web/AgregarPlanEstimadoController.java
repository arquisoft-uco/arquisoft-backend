package com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.interactor.AgregarPlanEstimadoInteractor;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.dto.AgregarPlanEstimadoRequestDTO;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.dto.AgregarPlanEstimadoResponseDTO;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.mapper.AgregarPlanEstimadoRequestMapper;
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
@RequestMapping("${rutas.mapas-ruta.tareas.base:/tareas}")
@RequiredArgsConstructor
@Tag(
        name = MapasRutaApiMessages.PlanEstimado.TAG_NAME,
        description = MapasRutaApiMessages.PlanEstimado.TAG_DESCRIPTION)
public class AgregarPlanEstimadoController {

    private final AgregarPlanEstimadoInteractor interactor;

    @PostMapping("${rutas.mapas-ruta.tareas.planes-estimados:/{tareaId}/planes-estimados}")
    @Operation(
            summary = MapasRutaApiMessages.PlanEstimado.AGREGAR_SUMMARY,
            description = MapasRutaApiMessages.PlanEstimado.AGREGAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = MapasRutaApiMessages.PlanEstimado.AGREGAR_RESP_201,
                    content = @Content(schema = @Schema(implementation = AgregarPlanEstimadoResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = MapasRutaApiMessages.PlanEstimado.AGREGAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = MapasRutaApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = MapasRutaApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = MapasRutaApiMessages.PlanEstimado.AGREGAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<AgregarPlanEstimadoResponseDTO> agregar(
            @PathVariable UUID tareaId,
            @RequestBody AgregarPlanEstimadoRequestDTO request) {
        var id = interactor.ejecutar(AgregarPlanEstimadoRequestMapper.toCommand(request, tareaId));
        return ResponseEntity.status(HttpStatus.CREATED).body(new AgregarPlanEstimadoResponseDTO(id));
    }
}
