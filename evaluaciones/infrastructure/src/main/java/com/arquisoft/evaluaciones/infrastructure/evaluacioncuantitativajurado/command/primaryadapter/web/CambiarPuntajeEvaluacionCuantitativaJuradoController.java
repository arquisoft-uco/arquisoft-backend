package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.interactor.CambiarPuntajeEvaluacionCuantitativaJuradoInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.dto.CambiarPuntajeEvaluacionCuantitativaJuradoRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.mapper.CambiarPuntajeEvaluacionCuantitativaJuradoRequestMapper;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.evaluaciones.evaluaciones-cuantitativas-jurado.base:/evaluaciones/evaluaciones-cuantitativas-jurado}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.TAG_DESCRIPTION)
public class CambiarPuntajeEvaluacionCuantitativaJuradoController {

    private final CambiarPuntajeEvaluacionCuantitativaJuradoInteractor interactor;

    @PatchMapping("${rutas.evaluaciones.evaluaciones-cuantitativas-jurado.puntaje:/{id}/puntaje}")
    @PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_EVALUACION_CUANTITATIVA_JURADO_UPDATE)
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.CAMBIAR_PUNTAJE_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.CAMBIAR_PUNTAJE_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.CAMBIAR_PUNTAJE_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.CAMBIAR_PUNTAJE_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.CAMBIAR_PUNTAJE_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> cambiarPuntaje(
            @PathVariable UUID id,
            @RequestBody CambiarPuntajeEvaluacionCuantitativaJuradoRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        interactor.ejecutar(
                CambiarPuntajeEvaluacionCuantitativaJuradoRequestMapper.toCommand(request, id, jwt.getSubject()));
        return ResponseEntity.noContent().build();
    }
}
