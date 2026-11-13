package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.interactor.OmitirEvaluacionesCualitativasAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web.mapper.OmitirEvaluacionesCualitativasAsesorRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EvaluacionesApiMessages;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${rutas.evaluaciones.evaluaciones.base:/evaluaciones/evaluaciones-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.TAG_DESCRIPTION)
public class OmitirEvaluacionesCualitativasAsesorController {

    private final OmitirEvaluacionesCualitativasAsesorInteractor interactor;

    @DeleteMapping("${rutas.evaluaciones.evaluaciones.cualitativas:/{evaluacionAsesorId}/cualitativas}")
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.OMITIR_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.OMITIR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.OMITIR_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.OMITIR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.OMITIR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> omitir(
            @PathVariable UUID evaluacionAsesorId,
            @Parameter(
                    description = EvaluacionesApiMessages.EvaluacionCualitativaAsesor.OMITIR_PARAM_ITEM,
                    array = @ArraySchema(schema = @Schema(type = "string", format = "uuid")))
            @RequestParam(name = EvaluacionesFields.OmisionEvaluacionesCualitativasAsesor.ITEMS, required = false)
            List<String> item,
            IdentidadActual identidad) {

        interactor.ejecutar(OmitirEvaluacionesCualitativasAsesorRequestMapper.toCommand(
                item, evaluacionAsesorId, identidad.id()));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
