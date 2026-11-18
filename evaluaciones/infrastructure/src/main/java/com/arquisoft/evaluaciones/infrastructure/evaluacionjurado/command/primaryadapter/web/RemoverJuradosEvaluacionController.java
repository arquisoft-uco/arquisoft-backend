package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.interactor.RemoverJuradosEvaluacionInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.primaryadapter.web.dto.RemoverJuradosEvaluacionRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.primaryadapter.web.mapper.RemoverJuradosEvaluacionRequestMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.evaluaciones.evaluacion.base:/evaluaciones}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EvaluacionJurado.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionJurado.TAG_DESCRIPTION)
public class RemoverJuradosEvaluacionController {

    private final RemoverJuradosEvaluacionInteractor interactor;

    @DeleteMapping("${rutas.evaluaciones.evaluacion.jurados:/{evaluacionId}/jurados}")
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionJurado.REMOVER_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionJurado.REMOVER_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = EvaluacionesApiMessages.EvaluacionJurado.REMOVER_REQUEST_BODY,
            required = true,
            content = @Content(schema = @Schema(implementation = RemoverJuradosEvaluacionRequestDTO.class)))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = EvaluacionesApiMessages.EvaluacionJurado.REMOVER_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionJurado.REMOVER_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionJurado.REMOVER_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> remover(
            @PathVariable UUID evaluacionId,
            @RequestBody RemoverJuradosEvaluacionRequestDTO request) {
        interactor.ejecutar(RemoverJuradosEvaluacionRequestMapper.toCommand(request, evaluacionId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
