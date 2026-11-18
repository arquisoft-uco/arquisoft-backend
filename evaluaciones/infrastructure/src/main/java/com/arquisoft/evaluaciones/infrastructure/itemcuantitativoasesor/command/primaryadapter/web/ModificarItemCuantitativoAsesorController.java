package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.interactor.ModificarItemCuantitativoAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.primaryadapter.web.dto.ModificarItemCuantitativoAsesorRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.primaryadapter.web.mapper.ModificarItemCuantitativoAsesorRequestMapper;
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
@RequestMapping("${rutas.evaluaciones.items-cuantitativos-asesor.base:/evaluaciones/items-cuantitativos-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.ItemCuantitativoAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.ItemCuantitativoAsesor.TAG_DESCRIPTION)
public class ModificarItemCuantitativoAsesorController {

    private final ModificarItemCuantitativoAsesorInteractor interactor;

    @PatchMapping("${rutas.evaluaciones.items-cuantitativos-asesor.por-id:/{itemId}}")
    @Operation(
            summary = EvaluacionesApiMessages.ItemCuantitativoAsesor.MODIFICAR_SUMMARY,
            description = EvaluacionesApiMessages.ItemCuantitativoAsesor.MODIFICAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = EvaluacionesApiMessages.ItemCuantitativoAsesor.MODIFICAR_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.ItemCuantitativoAsesor.MODIFICAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.ItemCuantitativoAsesor.MODIFICAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> modificar(
            @PathVariable UUID itemId,
            @RequestBody ModificarItemCuantitativoAsesorRequestDTO request) {
        interactor.ejecutar(ModificarItemCuantitativoAsesorRequestMapper.toCommand(request, itemId));
        return ResponseEntity.noContent().build();
    }
}
