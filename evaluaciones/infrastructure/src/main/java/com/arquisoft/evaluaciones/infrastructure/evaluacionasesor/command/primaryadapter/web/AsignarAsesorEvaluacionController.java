package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.primaryport.interactor.AsignarAsesorEvaluacionInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.primaryadapter.web.dto.AsignarAsesorEvaluacionRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.primaryadapter.web.mapper.AsignarAsesorEvaluacionRequestMapper;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.evaluaciones.evaluacion.base:/evaluaciones}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.EvaluacionAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.EvaluacionAsesor.TAG_DESCRIPTION)
public class AsignarAsesorEvaluacionController {

    private final AsignarAsesorEvaluacionInteractor interactor;

    @PatchMapping("${rutas.evaluaciones.evaluacion.asesor:/{evaluacionId}/asesor}")
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionAsesor.ASIGNAR_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionAsesor.ASIGNAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = EvaluacionesApiMessages.EvaluacionAsesor.ASIGNAR_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionAsesor.ASIGNAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionAsesor.ASIGNAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> asignar(
            @PathVariable UUID evaluacionId,
            @RequestBody AsignarAsesorEvaluacionRequestDTO request) {
        interactor.ejecutar(AsignarAsesorEvaluacionRequestMapper.toCommand(request, evaluacionId));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
