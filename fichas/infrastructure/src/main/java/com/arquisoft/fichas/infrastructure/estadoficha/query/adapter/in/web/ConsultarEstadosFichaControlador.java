package com.arquisoft.fichas.infrastructure.estadoficha.query.adapter.in.web;

import com.arquisoft.shared.message.annotation.FichasApiKeys;
import com.arquisoft.fichas.application.estadoficha.query.usecase.ConsultarEstadosFichaUseCase;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.fichas.infrastructure.web.FichasRoutes;
import com.arquisoft.shared.web.openapi.ApiCodes;
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
@Tag(name = FichasApiKeys.EstadoFicha.TAG_NAME, description = FichasApiKeys.EstadoFicha.TAG_DESCRIPTION)
public class ConsultarEstadosFichaControlador {

    private final ConsultarEstadosFichaUseCase consultarEstadosFichaUseCase;

    @GetMapping("${rutas.fichas.fichas-perfil.estados-ficha:/estados-ficha}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ESTADO_FICHA_VIEW)
    @Operation(
            summary = FichasApiKeys.EstadoFicha.CONSULTAR_SUMMARY,
            description = FichasApiKeys.EstadoFicha.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = FichasApiKeys.EstadoFicha.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EstadoFichaReadModel.class)
                    )),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiKeys.EstadoFicha.CONSULTAR_RESP_401,
                    content = @Content),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiKeys.EstadoFicha.CONSULTAR_RESP_403,
                    content = @Content)
    })
    public ResponseEntity<List<EstadoFichaReadModel>> consultarEstadosFicha() {
        List<EstadoFichaReadModel> estados = consultarEstadosFichaUseCase.ejecutar(null);
        return ResponseEntity.ok(estados);
    }
}
