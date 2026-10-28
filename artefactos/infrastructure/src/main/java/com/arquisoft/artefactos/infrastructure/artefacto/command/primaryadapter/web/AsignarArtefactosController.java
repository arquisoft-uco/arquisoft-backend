package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.AsignarArtefactosInteractor;
import com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.dto.AsignarArtefactosRequestDTO;
import com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.dto.AsignarArtefactosResponseDTO;
import com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.mapper.AsignarArtefactosRequestMapper;
import com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.mapper.AsignarArtefactosResponseMapper;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.artefactos.artefactos.base:/artefactos}")
@RequiredArgsConstructor
@Tag(
        name = ArtefactosApiMessages.Artefacto.TAG_NAME,
        description = ArtefactosApiMessages.Artefacto.TAG_DESCRIPTION)
public class AsignarArtefactosController {

    private final AsignarArtefactosInteractor asignarArtefactosInteractor;

    @PostMapping
    @Operation(
            summary = ArtefactosApiMessages.Artefacto.ASIGNAR_SUMMARY,
            description = ArtefactosApiMessages.Artefacto.ASIGNAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = ArtefactosApiMessages.Artefacto.ASIGNAR_RESP_201,
                    content = @Content(schema = @Schema(implementation = AsignarArtefactosResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.Artefacto.ASIGNAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = ArtefactosApiMessages.Artefacto.ASIGNAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<AsignarArtefactosResponseDTO> asignar(
            @RequestBody AsignarArtefactosRequestDTO request,
            IdentidadActual identidad) {
        var resultado = asignarArtefactosInteractor.ejecutar(
                AsignarArtefactosRequestMapper.toCommand(request, identidad.id()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AsignarArtefactosResponseMapper.toResponse(resultado));
    }
}
