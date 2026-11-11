package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.interactor.RegistrarEvaluacionesCuantitativasJuradoInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.dto.RegistrarEvaluacionesCuantitativasJuradoRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.mapper.RegistrarEvaluacionesCuantitativasJuradoRequestMapper;
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
@RequestMapping("${rutas.evaluaciones.evaluaciones-jurado.base:/evaluaciones/evaluaciones-jurado}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.TAG_DESCRIPTION)
public class RegistrarEvaluacionesCuantitativasJuradoController {

    private final RegistrarEvaluacionesCuantitativasJuradoInteractor interactor;

    @PostMapping("${rutas.evaluaciones.evaluaciones-jurado.cuantitativas:/{evaluacionJuradoId}/cuantitativas}")
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.REGISTRAR_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.REGISTRAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.REGISTRAR_RESP_201),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.REGISTRAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.REGISTRAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> registrar(
            @PathVariable UUID evaluacionJuradoId,
            @RequestBody RegistrarEvaluacionesCuantitativasJuradoRequestDTO request,
            IdentidadActual identidad) {

        interactor.ejecutar(RegistrarEvaluacionesCuantitativasJuradoRequestMapper.toCommand(
                request, evaluacionJuradoId, identidad.id()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
