package com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web;

import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.interactor.ConsultarVersionArtefactoInteractor;
import com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web.dto.VersionArtefactoResponseDTO;
import com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web.mapper.ConsultarVersionArtefactoRequestMapper;
import com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web.mapper.VersionArtefactoResponseMapper;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.artefactos.artefactos.base:/artefactos}")
@RequiredArgsConstructor
@Tag(name = ArtefactosApiMessages.VersionArtefacto.TAG_NAME, description = ArtefactosApiMessages.VersionArtefacto.TAG_DESCRIPTION)
public class ConsultarVersionArtefactoController {

    private final ConsultarVersionArtefactoInteractor consultarVersionArtefactoInteractor;

    @GetMapping("${rutas.artefactos.artefactos.version:/versiones/{versionArtefactoId}}")
    @Operation(
            summary = ArtefactosApiMessages.VersionArtefacto.CONSULTAR_DETALLE_SUMMARY,
            description = ArtefactosApiMessages.VersionArtefacto.CONSULTAR_DETALLE_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = ArtefactosApiMessages.VersionArtefacto.CONSULTAR_DETALLE_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VersionArtefactoResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ArtefactosApiMessages.VersionArtefacto.CONSULTAR_DETALLE_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ArtefactosApiMessages.VersionArtefacto.CONSULTAR_DETALLE_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = ArtefactosApiMessages.VersionArtefacto.CONSULTAR_DETALLE_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<VersionArtefactoResponseDTO> consultarVersionArtefacto(
            @PathVariable UUID versionArtefactoId,
            IdentidadActual identidad) {

        var version = consultarVersionArtefactoInteractor.ejecutar(
                ConsultarVersionArtefactoRequestMapper.toQuery(versionArtefactoId, identidad));

        return ResponseEntity.ok(VersionArtefactoResponseMapper.toResponse(version));
    }
}
