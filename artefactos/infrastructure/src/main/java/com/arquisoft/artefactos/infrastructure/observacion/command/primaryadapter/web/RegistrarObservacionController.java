package com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web;

import com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.RegistrarObservacionInteractor;
import com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web.dto.RegistrarObservacionRequestDTO;
import com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web.dto.RegistrarObservacionResponseDTO;
import com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web.mapper.RegistrarObservacionRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ArtefactosApiMessages;
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
@RequestMapping("${rutas.artefactos.artefactos.base:/artefactos}")
@RequiredArgsConstructor
@Tag(name = ArtefactosApiMessages.Observacion.TAG_NAME, description = ArtefactosApiMessages.Observacion.TAG_DESCRIPTION)
public class RegistrarObservacionController {

    private final RegistrarObservacionInteractor registrarObservacionInteractor;

    @PostMapping("${rutas.artefactos.artefactos.observaciones-revision:/revisiones/{revisionAsesorId}/observaciones}")
    @Operation(
            summary = ArtefactosApiMessages.Observacion.REGISTRAR_SUMMARY,
            description = ArtefactosApiMessages.Observacion.REGISTRAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = ArtefactosApiMessages.Observacion.REGISTRAR_RESP_201,
                    content = @Content(schema = @Schema(implementation = RegistrarObservacionResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.Observacion.REGISTRAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = ArtefactosApiMessages.Observacion.REGISTRAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<RegistrarObservacionResponseDTO> registrar(
            @PathVariable UUID revisionAsesorId,
            @RequestBody RegistrarObservacionRequestDTO request,
            IdentidadActual identidad) {
        var id = registrarObservacionInteractor.ejecutar(
                RegistrarObservacionRequestMapper.toCommand(revisionAsesorId, request, identidad.id()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegistrarObservacionResponseDTO(id));
    }
}
