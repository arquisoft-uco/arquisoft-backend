package com.arquisoft.fichas.infrastructure.estadoficha.query.primaryadapter.web;

import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.fichas.application.estadoficha.query.primaryport.interactor.ConsultarEstadosFichaInteractor;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.fichas.infrastructure.estadoficha.query.primaryadapter.web.dto.EstadoFichaResponseDTO;
import com.arquisoft.fichas.infrastructure.estadoficha.query.primaryadapter.web.mapper.EstadoFichaResponseMapper;
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
@Tag(name = FichasApiMessages.EstadoFicha.TAG_NAME, description = FichasApiMessages.EstadoFicha.TAG_DESCRIPTION)
public class ConsultarEstadosFichaController {

    private final ConsultarEstadosFichaInteractor consultarEstadosFichaInteractor;

    @GetMapping("${rutas.fichas.fichas-perfil.estados-ficha:/estados-ficha}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ESTADO_FICHA_VIEW)
    @Operation(
            summary = FichasApiMessages.EstadoFicha.CONSULTAR_SUMMARY,
            description = FichasApiMessages.EstadoFicha.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = FichasApiMessages.EstadoFicha.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EstadoFichaResponseDTO.class)
                    )),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.EstadoFicha.CONSULTAR_RESP_401,
                    content = @Content),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.EstadoFicha.CONSULTAR_RESP_403,
                    content = @Content)
    })
    public ResponseEntity<List<EstadoFichaResponseDTO>> consultarEstadosFicha() {
        List<EstadoFichaReadModel> estados = consultarEstadosFichaInteractor.ejecutar();

        return ResponseEntity.ok(estados.stream()
                .map(EstadoFichaResponseMapper::toResponse)
                .toList());
    }
}
