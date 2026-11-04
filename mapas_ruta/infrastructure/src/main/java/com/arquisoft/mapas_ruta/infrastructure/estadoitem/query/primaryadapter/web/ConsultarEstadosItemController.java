package com.arquisoft.mapas_ruta.infrastructure.estadoitem.query.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.estadoitem.query.primaryport.interactor.ConsultarEstadosItemInteractor;
import com.arquisoft.mapas_ruta.infrastructure.estadoitem.query.primaryadapter.web.dto.EstadoItemResponseDTO;
import com.arquisoft.mapas_ruta.infrastructure.estadoitem.query.primaryadapter.web.mapper.EstadoItemResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.MapasRutaApiMessages;
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
@RequestMapping("${rutas.mapas-ruta.estados-item.base:/estados-item}")
@RequiredArgsConstructor
@Tag(name = MapasRutaApiMessages.EstadoItem.TAG_NAME,
        description = MapasRutaApiMessages.EstadoItem.TAG_DESCRIPTION)
public class ConsultarEstadosItemController {

    private final ConsultarEstadosItemInteractor consultarEstadosItemInteractor;

    @GetMapping
    @Operation(
            summary = MapasRutaApiMessages.EstadoItem.CONSULTAR_SUMMARY,
            description = MapasRutaApiMessages.EstadoItem.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = MapasRutaApiMessages.EstadoItem.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EstadoItemResponseDTO.class)
                    )),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = MapasRutaApiMessages.Comun.RESP_401,
                    content = @Content),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = MapasRutaApiMessages.Comun.RESP_403,
                    content = @Content)
    })
    public ResponseEntity<List<EstadoItemResponseDTO>> consultarEstadosItem() {
        var estados = consultarEstadosItemInteractor.ejecutar();

        return ResponseEntity.ok(estados.stream()
                .map(EstadoItemResponseMapper::toResponse)
                .toList());
    }
}
