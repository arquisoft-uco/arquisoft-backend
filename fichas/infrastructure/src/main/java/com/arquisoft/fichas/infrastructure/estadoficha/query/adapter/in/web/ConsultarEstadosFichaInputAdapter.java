package com.arquisoft.fichas.infrastructure.estadoficha.query.adapter.in.web;

import com.arquisoft.fichas.application.estadoficha.query.port.in.ConsultarEstadosFichaInputPort;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
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
@RequestMapping("/fichas-perfil")
@RequiredArgsConstructor
@Tag(name = "Fichas de Perfil", description = "Gestión del ciclo de vida de las fichas de perfil")
public class ConsultarEstadosFichaInputAdapter {

    private final ConsultarEstadosFichaInputPort inputPort;

    @GetMapping("/estados-ficha")
    @PreAuthorize("hasAuthority('fichas:estado-ficha:view')")
    @Operation(
            summary = "Consultar todos los estados ficha",
            description = "Retorna todos los estados ficha disponibles en el catálogo sin filtros ni paginación",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de estados ficha retornada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EstadoFichaReadModel.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado - token JWT ausente o inválido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autorizado - client role insuficiente",
                    content = @Content
            )
    })
    public ResponseEntity<List<EstadoFichaReadModel>> consultarEstadosFicha() {
        List<EstadoFichaReadModel> estados = inputPort.ejecutar(null);
        return ResponseEntity.ok(estados);
    }
}
