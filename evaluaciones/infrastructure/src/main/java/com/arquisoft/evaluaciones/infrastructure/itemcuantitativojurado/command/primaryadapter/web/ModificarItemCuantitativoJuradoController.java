package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.interactor.ModificarItemCuantitativoJuradoInteractor;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.primaryadapter.web.dto.ModificarItemCuantitativoJuradoRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.primaryadapter.web.mapper.ModificarItemCuantitativoJuradoRequestMapper;
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
@RequestMapping("${rutas.evaluaciones.items-cuantitativos-jurado.base:/evaluaciones/items-cuantitativos-jurado}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.ItemCuantitativoJurado.TAG_NAME,
        description = EvaluacionesApiMessages.ItemCuantitativoJurado.TAG_DESCRIPTION)
public class ModificarItemCuantitativoJuradoController {

    private final ModificarItemCuantitativoJuradoInteractor interactor;

    @PatchMapping("${rutas.evaluaciones.items-cuantitativos-jurado.por-id:/{itemId}}")
    @PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_ITEM_CUANTITATIVO_JURADO_UPDATE)
    @Operation(
            summary = EvaluacionesApiMessages.ItemCuantitativoJurado.MODIFICAR_SUMMARY,
            description = EvaluacionesApiMessages.ItemCuantitativoJurado.MODIFICAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = EvaluacionesApiMessages.ItemCuantitativoJurado.MODIFICAR_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.ItemCuantitativoJurado.MODIFICAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.ItemCuantitativoJurado.MODIFICAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> modificar(
            @PathVariable UUID itemId,
            @RequestBody ModificarItemCuantitativoJuradoRequestDTO request) {
        interactor.ejecutar(ModificarItemCuantitativoJuradoRequestMapper.toCommand(request, itemId));
        return ResponseEntity.noContent().build();
    }
}
