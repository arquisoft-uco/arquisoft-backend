package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.interactor.ModificarItemCualitativoJuradoInteractor;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.primaryadapter.web.dto.ModificarItemCualitativoJuradoRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.primaryadapter.web.mapper.ModificarItemCualitativoJuradoRequestMapper;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.evaluaciones.items-cualitativos-jurado.base:/evaluaciones/items-cualitativos-jurado}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.ItemCualitativoJurado.TAG_NAME,
        description = EvaluacionesApiMessages.ItemCualitativoJurado.TAG_DESCRIPTION)
public class ModificarItemCualitativoJuradoController {

    private final ModificarItemCualitativoJuradoInteractor interactor;

    @PatchMapping("${rutas.evaluaciones.items-cualitativos-jurado.por-id:/{itemId}}")
    @PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_ITEM_CUALITATIVO_JURADO_UPDATE)
    @Operation(
            summary = EvaluacionesApiMessages.ItemCualitativoJurado.MODIFICAR_SUMMARY,
            description = EvaluacionesApiMessages.ItemCualitativoJurado.MODIFICAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = EvaluacionesApiMessages.ItemCualitativoJurado.MODIFICAR_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.ItemCualitativoJurado.MODIFICAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.ItemCualitativoJurado.MODIFICAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> modificar(
            @PathVariable UUID itemId,
            @RequestBody ModificarItemCualitativoJuradoRequestDTO request) {
        interactor.ejecutar(ModificarItemCualitativoJuradoRequestMapper.toCommand(request, itemId));
        return ResponseEntity.noContent().build();
    }
}
