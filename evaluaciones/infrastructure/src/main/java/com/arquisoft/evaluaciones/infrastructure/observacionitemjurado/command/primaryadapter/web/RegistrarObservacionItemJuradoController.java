package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.interactor.RegistrarObservacionItemJuradoInteractor;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web.dto.RegistrarObservacionItemJuradoRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web.dto.RegistrarObservacionItemJuradoResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web.mapper.RegistrarObservacionItemJuradoRequestMapper;
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
@RequestMapping("${rutas.evaluaciones.evaluaciones-cuantitativas-jurado.base:/evaluaciones/evaluaciones-cuantitativas-jurado}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.ObservacionItemJurado.TAG_NAME,
        description = EvaluacionesApiMessages.ObservacionItemJurado.TAG_DESCRIPTION)
public class RegistrarObservacionItemJuradoController {

    private final RegistrarObservacionItemJuradoInteractor interactor;

    @PostMapping("${rutas.evaluaciones.evaluaciones-cuantitativas-jurado.observaciones:/{id}/observaciones}")
    @PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_OBSERVACION_ITEM_JURADO_CREATE)
    @Operation(
            summary = EvaluacionesApiMessages.ObservacionItemJurado.REGISTRAR_SUMMARY,
            description = EvaluacionesApiMessages.ObservacionItemJurado.REGISTRAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = EvaluacionesApiMessages.ObservacionItemJurado.REGISTRAR_RESP_201,
                    content = @Content(schema = @Schema(
                            implementation = RegistrarObservacionItemJuradoResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.ObservacionItemJurado.REGISTRAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = EvaluacionesApiMessages.ObservacionItemJurado.REGISTRAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<RegistrarObservacionItemJuradoResponseDTO> registrar(
            @PathVariable UUID id,
            @RequestBody RegistrarObservacionItemJuradoRequestDTO request) {
        UUID observacionId = interactor.ejecutar(
                RegistrarObservacionItemJuradoRequestMapper.toCommand(request, id));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegistrarObservacionItemJuradoResponseDTO(observacionId));
    }
}
