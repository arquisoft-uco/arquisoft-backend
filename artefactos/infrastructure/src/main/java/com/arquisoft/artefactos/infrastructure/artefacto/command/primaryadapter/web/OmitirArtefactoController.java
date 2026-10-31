package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.OmitirArtefactoInteractor;
import com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.mapper.OmitirArtefactoRequestMapper;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.artefactos.artefactos.base:/artefactos}")
@RequiredArgsConstructor
@Tag(
        name = ArtefactosApiMessages.Artefacto.TAG_NAME,
        description = ArtefactosApiMessages.Artefacto.TAG_DESCRIPTION)
public class OmitirArtefactoController {

    private final OmitirArtefactoInteractor omitirArtefactoInteractor;

    @DeleteMapping("${rutas.artefactos.artefactos.omitir:/{artefactoId}}")
    @Operation(
            summary = ArtefactosApiMessages.Artefacto.OMITIR_SUMMARY,
            description = ArtefactosApiMessages.Artefacto.OMITIR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = ArtefactosApiMessages.Artefacto.OMITIR_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.Artefacto.OMITIR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = ArtefactosApiMessages.Artefacto.OMITIR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> omitir(
            @PathVariable UUID artefactoId,
            IdentidadActual identidad) {
        omitirArtefactoInteractor.ejecutar(OmitirArtefactoRequestMapper.toCommand(artefactoId, identidad.id()));
        return ResponseEntity.noContent().build();
    }
}
