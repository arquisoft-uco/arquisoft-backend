package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.interactor.RegistrarEvaluacionesCualitativasAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web.dto.RegistrarEvaluacionesCualitativasAsesorRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web.mapper.RegistrarEvaluacionesCualitativasAsesorRequestMapper;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.evaluaciones.evaluaciones-asesor.base:/evaluaciones-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.TAG_DESCRIPTION)
public class RegistrarEvaluacionesCualitativasAsesorController {

    private final RegistrarEvaluacionesCualitativasAsesorInteractor interactor;

    @PostMapping("${rutas.evaluaciones.evaluaciones-asesor.cualitativas:/{evaluacionAsesorId}/cualitativas}")
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.REGISTRAR_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.REGISTRAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.REGISTRAR_RESP_201),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.REGISTRAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.REGISTRAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> registrar(
            @PathVariable UUID evaluacionAsesorId,
            @RequestBody RegistrarEvaluacionesCualitativasAsesorRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        interactor.ejecutar(RegistrarEvaluacionesCualitativasAsesorRequestMapper.toCommand(
                request, evaluacionAsesorId, jwt.getSubject()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
