package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.interactor.RegistrarEvaluacionesCualitativasJuradoInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web.dto.RegistrarEvaluacionesCualitativasJuradoRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web.mapper.RegistrarEvaluacionesCualitativasJuradoRequestMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.evaluaciones.registro-cualitativas-jurado.base:/evaluaciones-jurado}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EvaluacionCualitativaJurado.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionCualitativaJurado.TAG_DESCRIPTION)
public class RegistrarEvaluacionesCualitativasJuradoController {

    private final RegistrarEvaluacionesCualitativasJuradoInteractor interactor;

    @PostMapping("${rutas.evaluaciones.registro-cualitativas-jurado.registrar:/{evaluacionJuradoId}/evaluaciones-cualitativas}")
    @PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_EVALUACION_CUALITATIVA_JURADO_CREATE)
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionCualitativaJurado.REGISTRAR_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionCualitativaJurado.REGISTRAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaJurado.REGISTRAR_RESP_201),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaJurado.REGISTRAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaJurado.REGISTRAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> registrar(
            @PathVariable UUID evaluacionJuradoId,
            @RequestBody RegistrarEvaluacionesCualitativasJuradoRequestDTO request) {

        interactor.ejecutar(RegistrarEvaluacionesCualitativasJuradoRequestMapper.toCommand(
                request, evaluacionJuradoId));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
