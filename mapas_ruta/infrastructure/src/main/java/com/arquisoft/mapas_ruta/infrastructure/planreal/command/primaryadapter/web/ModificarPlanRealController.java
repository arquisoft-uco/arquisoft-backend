package com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.ModificarPlanRealInteractor;
import com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.web.dto.ModificarPlanRealRequestDTO;
import com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.web.mapper.ModificarPlanRealRequestMapper;
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
@RequestMapping("${rutas.mapas-ruta.mapas-ruta.base:/mapas-ruta}")
@RequiredArgsConstructor
@Tag(
        name = MapasRutaApiMessages.PlanReal.TAG_NAME,
        description = MapasRutaApiMessages.PlanReal.TAG_DESCRIPTION)
public class ModificarPlanRealController {

    private final ModificarPlanRealInteractor interactor;

    @PatchMapping("${rutas.mapas-ruta.mapas-ruta.planes-reales:/planes-reales/{planRealId}}")
    @Operation(
            summary = MapasRutaApiMessages.PlanReal.MODIFICAR_SUMMARY,
            description = MapasRutaApiMessages.PlanReal.MODIFICAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = MapasRutaApiMessages.PlanReal.MODIFICAR_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = MapasRutaApiMessages.PlanReal.MODIFICAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = MapasRutaApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = MapasRutaApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = MapasRutaApiMessages.PlanReal.MODIFICAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> modificar(
            @PathVariable UUID planRealId,
            @RequestBody ModificarPlanRealRequestDTO request) {
        interactor.ejecutar(ModificarPlanRealRequestMapper.toCommand(request, planRealId));
        return ResponseEntity.noContent().build();
    }
}
