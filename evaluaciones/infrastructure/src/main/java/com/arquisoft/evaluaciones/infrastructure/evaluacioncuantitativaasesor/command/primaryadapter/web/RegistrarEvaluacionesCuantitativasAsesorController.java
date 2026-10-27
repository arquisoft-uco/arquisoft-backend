package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.interactor.RegistrarEvaluacionesCuantitativasAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web.dto.RegistrarEvaluacionesCuantitativasAsesorRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web.mapper.RegistrarEvaluacionesCuantitativasAsesorRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EvaluacionesApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
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
        name = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.TAG_DESCRIPTION)
public class RegistrarEvaluacionesCuantitativasAsesorController {

    private final RegistrarEvaluacionesCuantitativasAsesorInteractor interactor;

    @PostMapping("${rutas.evaluaciones.evaluaciones-asesor.cuantitativas:/{evaluacionAsesorId}/cuantitativas}")
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.REGISTRAR_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.REGISTRAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.REGISTRAR_RESP_201),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.REGISTRAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaAsesor.REGISTRAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> registrar(
            @PathVariable UUID evaluacionAsesorId,
            @RequestBody RegistrarEvaluacionesCuantitativasAsesorRequestDTO request,
            IdentidadActual identidad) {

        interactor.ejecutar(RegistrarEvaluacionesCuantitativasAsesorRequestMapper.toCommand(
                request, evaluacionAsesorId, identidad.id()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
