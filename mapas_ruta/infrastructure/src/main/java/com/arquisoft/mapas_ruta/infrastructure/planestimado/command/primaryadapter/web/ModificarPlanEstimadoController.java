package com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.interactor.ModificarPlanEstimadoInteractor;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.dto.ModificarPlanEstimadoRequestDTO;
import com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web.mapper.ModificarPlanEstimadoRequestMapper;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.mapas-ruta.planes-estimados.base:/planes-estimados}")
@RequiredArgsConstructor
@Tag(
        name = MapasRutaApiMessages.PlanEstimado.TAG_NAME,
        description = MapasRutaApiMessages.PlanEstimado.TAG_DESCRIPTION)
public class ModificarPlanEstimadoController {

    private final ModificarPlanEstimadoInteractor interactor;

    @PatchMapping("${rutas.mapas-ruta.planes-estimados.por-id:/{planEstimadoId}}")
    @Operation(
            summary = MapasRutaApiMessages.PlanEstimado.MODIFICAR_SUMMARY,
            description = MapasRutaApiMessages.PlanEstimado.MODIFICAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = MapasRutaApiMessages.PlanEstimado.MODIFICAR_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = MapasRutaApiMessages.PlanEstimado.MODIFICAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = MapasRutaApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = MapasRutaApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = MapasRutaApiMessages.PlanEstimado.MODIFICAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> modificar(
            @PathVariable UUID planEstimadoId,
            @RequestBody ModificarPlanEstimadoRequestDTO request) {
        interactor.ejecutar(ModificarPlanEstimadoRequestMapper.toCommand(request, planEstimadoId));
        return ResponseEntity.noContent().build();
    }
}
