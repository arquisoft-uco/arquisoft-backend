package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.interactor.ModificarObservacionItemJuradoInteractor;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web.dto.ModificarObservacionItemJuradoRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web.mapper.ModificarObservacionItemJuradoRequestMapper;
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
@RequestMapping("${rutas.evaluaciones.observaciones-item-jurado.base:/evaluaciones/observaciones-item-jurado}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.ObservacionItemJurado.TAG_NAME,
        description = EvaluacionesApiMessages.ObservacionItemJurado.TAG_DESCRIPTION)
public class ModificarObservacionItemJuradoController {

    private final ModificarObservacionItemJuradoInteractor interactor;

    @PatchMapping("${rutas.evaluaciones.observaciones-item-jurado.por-id:/{observacionId}}")
    @PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_OBSERVACION_ITEM_JURADO_UPDATE)
    @Operation(
            summary = EvaluacionesApiMessages.ObservacionItemJurado.MODIFICAR_SUMMARY,
            description = EvaluacionesApiMessages.ObservacionItemJurado.MODIFICAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = EvaluacionesApiMessages.ObservacionItemJurado.MODIFICAR_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.ObservacionItemJurado.MODIFICAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.ObservacionItemJurado.MODIFICAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> modificar(
            @PathVariable UUID observacionId,
            @RequestBody ModificarObservacionItemJuradoRequestDTO request) {
        interactor.ejecutar(ModificarObservacionItemJuradoRequestMapper.toCommand(request, observacionId));
        return ResponseEntity.noContent().build();
    }
}
