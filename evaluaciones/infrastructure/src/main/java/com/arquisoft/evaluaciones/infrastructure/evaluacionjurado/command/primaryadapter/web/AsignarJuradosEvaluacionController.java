package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.interactor.AsignarJuradosEvaluacionInteractor;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.primaryadapter.web.dto.AsignarJuradosEvaluacionRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.primaryadapter.web.dto.AsignarJuradosEvaluacionResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.primaryadapter.web.mapper.AsignarJuradosEvaluacionRequestMapper;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
public class AsignarJuradosEvaluacionController {

    private final AsignarJuradosEvaluacionInteractor interactor;

    @PostMapping("${rutas.evaluaciones.evaluacion.jurados:/{evaluacionId}/jurados}")
    @Operation(
            summary = EvaluacionesApiMessages.EvaluacionJurado.ASIGNAR_SUMMARY,
            description = EvaluacionesApiMessages.EvaluacionJurado.ASIGNAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = EvaluacionesApiMessages.EvaluacionJurado.ASIGNAR_RESP_201),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.EvaluacionJurado.ASIGNAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.EvaluacionJurado.ASIGNAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<AsignarJuradosEvaluacionResponseDTO> asignar(
            @PathVariable UUID evaluacionId,
            @RequestBody AsignarJuradosEvaluacionRequestDTO request) {
        var evaluacionesJurado = interactor.ejecutar(
                AsignarJuradosEvaluacionRequestMapper.toCommand(request, evaluacionId));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AsignarJuradosEvaluacionResponseDTO(evaluacionesJurado));
    }
}
