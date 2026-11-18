package com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.RemoverPlanRealInteractor;
import com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.web.mapper.RemoverPlanRealRequestMapper;
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
@RequestMapping("${rutas.mapas-ruta.mapas-ruta.base:/mapas-ruta}")
@RequiredArgsConstructor
@Tag(
        name = MapasRutaApiMessages.PlanReal.TAG_NAME,
        description = MapasRutaApiMessages.PlanReal.TAG_DESCRIPTION)
public class RemoverPlanRealController {

    private final RemoverPlanRealInteractor interactor;

    @DeleteMapping("${rutas.mapas-ruta.mapas-ruta.planes-reales:/planes-reales/{planRealId}}")
    @Operation(
            summary = MapasRutaApiMessages.PlanReal.REMOVER_SUMMARY,
            description = MapasRutaApiMessages.PlanReal.REMOVER_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = MapasRutaApiMessages.PlanReal.REMOVER_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = MapasRutaApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = MapasRutaApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = MapasRutaApiMessages.PlanReal.REMOVER_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> remover(@PathVariable UUID planRealId) {
        interactor.ejecutar(RemoverPlanRealRequestMapper.toCommand(planRealId));
        return ResponseEntity.noContent().build();
    }
}
