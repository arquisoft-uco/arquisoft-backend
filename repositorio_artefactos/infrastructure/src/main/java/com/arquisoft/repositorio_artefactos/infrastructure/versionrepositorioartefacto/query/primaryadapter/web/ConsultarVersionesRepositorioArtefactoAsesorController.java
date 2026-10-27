package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.interactor.ConsultarVersionesRepositorioArtefactoAsesorInteractor;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.dto.VersionRepositorioArtefactoAsesorResponseDTO;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.mapper.ConsultarVersionesRepositorioArtefactoAsesorRequestMapper;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.mapper.VersionRepositorioArtefactoAsesorResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.RepositorioArtefactosApiMessages;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.dto.PageResponseDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.repositorio-artefactos.repositorio-artefactos.base:/repositorio-artefactos}")
@RequiredArgsConstructor
@Tag(
        name = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.TAG_NAME,
        description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.TAG_DESCRIPTION)
public class ConsultarVersionesRepositorioArtefactoAsesorController {

    private final ConsultarVersionesRepositorioArtefactoAsesorInteractor interactor;

    @PostMapping("${rutas.repositorio-artefactos.repositorio-artefactos.versiones-asesor:/versiones/asesor}")
    @Operation(
            summary = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.CONSULTAR_ASESOR_SUMMARY,
            description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.CONSULTAR_ASESOR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.CONSULTAR_ASESOR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = RepositorioArtefactosApiMessages.VersionRepositorioArtefacto.CONSULTAR_ASESOR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = RepositorioArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = RepositorioArtefactosApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<PageResponseDTO<VersionRepositorioArtefactoAsesorResponseDTO>> consultar(
            @RequestBody(required = false) QueryCriteriaRequestDTO request) {

        var resultado = interactor.ejecutar(
                ConsultarVersionesRepositorioArtefactoAsesorRequestMapper.toQuery(request));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(VersionRepositorioArtefactoAsesorResponseMapper::toResponse)));
    }
}
