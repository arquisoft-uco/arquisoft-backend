package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.interactor.GenerarRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web.dto.GenerarRepositorioArtefactoRequestDTO;
import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web.dto.GenerarRepositorioArtefactoResponseDTO;
import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web.mapper.GenerarRepositorioArtefactoRequestMapper;
import com.arquisoft.repositorio_artefactos.infrastructure.security.RepositorioArtefactosAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.RepositorioArtefactosApiMessages;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.repositorio-artefactos.repositorio-artefactos.base:/repositorio-artefactos}")
@RequiredArgsConstructor
@Tag(
        name = RepositorioArtefactosApiMessages.RepositorioArtefacto.TAG_NAME,
        description = RepositorioArtefactosApiMessages.RepositorioArtefacto.TAG_DESCRIPTION)
public class GenerarRepositorioArtefactoController {

    private final GenerarRepositorioArtefactoInteractor interactor;

    @PostMapping
    @PreAuthorize(RepositorioArtefactosAuthorities.Expresiones.HAS_REPOSITORIO_ARTEFACTO_CREATE)
    @Operation(
            summary = RepositorioArtefactosApiMessages.RepositorioArtefacto.GENERAR_SUMMARY,
            description = RepositorioArtefactosApiMessages.RepositorioArtefacto.GENERAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = RepositorioArtefactosApiMessages.RepositorioArtefacto.GENERAR_RESP_201,
                    content = @Content(schema = @Schema(
                            implementation = GenerarRepositorioArtefactoResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = RepositorioArtefactosApiMessages.RepositorioArtefacto.GENERAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = RepositorioArtefactosApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = RepositorioArtefactosApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = RepositorioArtefactosApiMessages.RepositorioArtefacto.GENERAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<GenerarRepositorioArtefactoResponseDTO> generar(
            @RequestBody GenerarRepositorioArtefactoRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        var id = interactor.ejecutar(
                GenerarRepositorioArtefactoRequestMapper.toCommand(request, jwt.getSubject()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenerarRepositorioArtefactoResponseDTO(id));
    }
}
