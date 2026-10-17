package com.arquisoft.fichas.infrastructure.estadoobservacionrevision.query.primaryadapter.web;

import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.fichas.application.estadoobservacionrevision.query.primaryport.interactor.ConsultarEstadosObservacionRevisionInteractor;
import com.arquisoft.fichas.infrastructure.estadoobservacionrevision.query.primaryadapter.web.dto.EstadoObservacionRevisionResponseDTO;
import com.arquisoft.fichas.infrastructure.estadoobservacionrevision.query.primaryadapter.web.mapper.EstadoObservacionRevisionResponseMapper;
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
@Tag(name = FichasApiMessages.EstadoObservacionRevision.TAG_NAME, description = FichasApiMessages.EstadoObservacionRevision.TAG_DESCRIPTION)
public class ConsultarEstadosObservacionRevisionController {

    private final ConsultarEstadosObservacionRevisionInteractor consultarEstadosObservacionRevisionInteractor;

    @GetMapping("${rutas.fichas.fichas-perfil.estados-observacion-revision:/estados-observacion-revision}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ESTADO_OBSERVACION_REVISION_VIEW)
    @Operation(
            summary = FichasApiMessages.EstadoObservacionRevision.CONSULTAR_SUMMARY,
            description = FichasApiMessages.EstadoObservacionRevision.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = FichasApiMessages.EstadoObservacionRevision.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EstadoObservacionRevisionResponseDTO.class)
                    )),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.EstadoObservacionRevision.CONSULTAR_RESP_401,
                    content = @Content),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.EstadoObservacionRevision.CONSULTAR_RESP_403,
                    content = @Content)
    })
    public ResponseEntity<List<EstadoObservacionRevisionResponseDTO>> consultarEstadosObservacionRevision() {
        var estados = consultarEstadosObservacionRevisionInteractor.ejecutar();

        return ResponseEntity.ok(estados.stream()
                .map(EstadoObservacionRevisionResponseMapper::toResponse)
                .toList());
    }
}
