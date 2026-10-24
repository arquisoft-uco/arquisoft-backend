package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.interactor.ConsultarVersionesRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.dto.VersionRepositorioArtefactoResponseDTO;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.mapper.ConsultarVersionesRepositorioArtefactoRequestMapper;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.mapper.VersionRepositorioArtefactoResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.RepositorioArtefactosApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${rutas.repositorio-artefactos.repositorio-artefactos.base:/repositorio-artefactos}")
@RequiredArgsConstructor
@Tag(
        name = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.TAG_NAME,
        description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.TAG_DESCRIPTION)
public class ConsultarVersionesRepositorioArtefactoController {

    private final ConsultarVersionesRepositorioArtefactoInteractor interactor;

    @GetMapping("${rutas.repositorio-artefactos.repositorio-artefactos.versiones:/{repositorioId}/versiones}")
    @Operation(
            summary = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.CONSULTAR_SUMMARY,
            description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(implementation = VersionRepositorioArtefactoResponseDTO.class)))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = RepositorioArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = RepositorioArtefactosApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<List<VersionRepositorioArtefactoResponseDTO>> consultar(
            @Parameter(description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.PARAM_REPOSITORIO)
            @PathVariable UUID repositorioId) {
        var resultado = interactor.ejecutar(ConsultarVersionesRepositorioArtefactoRequestMapper.toQuery(repositorioId));
        return ResponseEntity.ok(resultado.stream()
                .map(VersionRepositorioArtefactoResponseMapper::toResponse)
                .toList());
    }
}
