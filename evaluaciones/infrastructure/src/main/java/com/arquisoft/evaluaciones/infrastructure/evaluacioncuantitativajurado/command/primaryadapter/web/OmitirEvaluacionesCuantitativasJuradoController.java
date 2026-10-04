package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.interactor.OmitirEvaluacionesCuantitativasJuradoInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.dto.OmitirEvaluacionesCuantitativasJuradoRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.mapper.OmitirEvaluacionesCuantitativasJuradoRequestMapper;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class OmitirEvaluacionesCuantitativasJuradoController {

    private final OmitirEvaluacionesCuantitativasJuradoInteractor interactor;

    @DeleteMapping("${rutas.evaluaciones.evaluaciones-jurado.cuantitativas:/{evaluacionJuradoId}/cuantitativas}")
    @PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_EVALUACION_CUANTITATIVA_JURADO_DELETE)
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.OMITIR_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.OMITIR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.OMITIR_REQUEST_BODY,
            required = true,
            content = @Content(schema = @Schema(implementation = OmitirEvaluacionesCuantitativasJuradoRequestDTO.class)))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.OMITIR_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.OMITIR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionCuantitativaJurado.OMITIR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> omitir(
            @PathVariable UUID evaluacionJuradoId,
            @RequestBody OmitirEvaluacionesCuantitativasJuradoRequestDTO request) {

        interactor.ejecutar(OmitirEvaluacionesCuantitativasJuradoRequestMapper.toCommand(
                request, evaluacionJuradoId));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
