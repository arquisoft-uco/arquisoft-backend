package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.interactor.ModificarItemCualitativoAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.primaryadapter.web.dto.ModificarItemCualitativoAsesorRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.primaryadapter.web.mapper.ModificarItemCualitativoAsesorRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EvaluacionesApiMessages;
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
@RequestMapping("${rutas.evaluaciones.items-cualitativos-asesor.base:/evaluaciones/items-cualitativos-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.ItemCualitativoAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.ItemCualitativoAsesor.TAG_DESCRIPTION)
public class ModificarItemCualitativoAsesorController {

    private final ModificarItemCualitativoAsesorInteractor interactor;

    @PatchMapping("${rutas.evaluaciones.items-cualitativos-asesor.por-id:/{itemId}}")
    @Operation(
            summary = EvaluacionesApiMessages.ItemCualitativoAsesor.MODIFICAR_SUMMARY,
            description = EvaluacionesApiMessages.ItemCualitativoAsesor.MODIFICAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = EvaluacionesApiMessages.ItemCualitativoAsesor.MODIFICAR_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.ItemCualitativoAsesor.MODIFICAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.ItemCualitativoAsesor.MODIFICAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> modificar(
            @PathVariable UUID itemId,
            @RequestBody ModificarItemCualitativoAsesorRequestDTO request) {
        interactor.ejecutar(ModificarItemCualitativoAsesorRequestMapper.toCommand(request, itemId));
        return ResponseEntity.noContent().build();
    }
}
