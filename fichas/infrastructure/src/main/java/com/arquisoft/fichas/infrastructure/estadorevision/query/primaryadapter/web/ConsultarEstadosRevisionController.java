package com.arquisoft.fichas.infrastructure.estadorevision.query.primaryadapter.web;

import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.fichas.application.estadorevision.query.primaryport.interactor.ConsultarEstadosRevisionInteractor;
import com.arquisoft.fichas.infrastructure.estadorevision.query.primaryadapter.web.dto.EstadoRevisionResponseDTO;
import com.arquisoft.fichas.infrastructure.estadorevision.query.primaryadapter.web.mapper.EstadoRevisionResponseMapper;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ApiCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.EstadoRevision.TAG_NAME, description = FichasApiMessages.EstadoRevision.TAG_DESCRIPTION)
public class ConsultarEstadosRevisionController {

    private final ConsultarEstadosRevisionInteractor consultarEstadosRevisionInteractor;

    @GetMapping("${rutas.fichas.fichas-perfil.estados-revision:/estados-revision}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ESTADO_REVISION_VIEW)
    @Operation(
            summary = FichasApiMessages.EstadoRevision.CONSULTAR_SUMMARY,
            description = FichasApiMessages.EstadoRevision.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = FichasApiMessages.EstadoRevision.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EstadoRevisionResponseDTO.class)
                    )),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.EstadoRevision.CONSULTAR_RESP_401,
                    content = @Content),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.EstadoRevision.CONSULTAR_RESP_403,
                    content = @Content)
    })
    public ResponseEntity<List<EstadoRevisionResponseDTO>> consultarEstadosRevision() {
        var estados = consultarEstadosRevisionInteractor.ejecutar();

        return ResponseEntity.ok(estados.stream()
                .map(EstadoRevisionResponseMapper::toResponse)
                .toList());
    }
}
