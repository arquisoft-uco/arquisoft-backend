package com.arquisoft.entregables.infrastructure.estadoentregable.query.primaryadapter.web;

import com.arquisoft.entregables.application.estadoentregable.query.primaryport.interactor.ConsultarEstadosEntregableInteractor;
import com.arquisoft.entregables.infrastructure.estadoentregable.query.primaryadapter.web.dto.EstadoEntregableResponseDTO;
import com.arquisoft.entregables.infrastructure.estadoentregable.query.primaryadapter.web.mapper.EstadoEntregableResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EntregablesApiMessages;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${rutas.entregables.estados-entregable.base:/estados-entregable}")
@RequiredArgsConstructor
@Tag(name = EntregablesApiMessages.EstadoEntregable.TAG_NAME,
        description = EntregablesApiMessages.EstadoEntregable.TAG_DESCRIPTION)
public class ConsultarEstadosEntregableController {

    private final ConsultarEstadosEntregableInteractor consultarEstadosEntregableInteractor;

    @GetMapping
    @Operation(
            summary = EntregablesApiMessages.EstadoEntregable.CONSULTAR_SUMMARY,
            description = EntregablesApiMessages.EstadoEntregable.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = EntregablesApiMessages.EstadoEntregable.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EstadoEntregableResponseDTO.class)
                    )),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = EntregablesApiMessages.Comun.RESP_401,
                    content = @Content),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = EntregablesApiMessages.Comun.RESP_403,
                    content = @Content)
    })
    public ResponseEntity<List<EstadoEntregableResponseDTO>> consultarEstadosEntregable() {
        var estados = consultarEstadosEntregableInteractor.ejecutar();

        return ResponseEntity.ok(estados.stream()
                .map(EstadoEntregableResponseMapper::toResponse)
                .toList());
    }
}
