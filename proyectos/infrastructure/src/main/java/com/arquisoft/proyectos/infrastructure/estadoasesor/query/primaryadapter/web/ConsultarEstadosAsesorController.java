package com.arquisoft.proyectos.infrastructure.estadoasesor.query.primaryadapter.web;

import com.arquisoft.proyectos.application.estadoasesor.query.primaryport.interactor.ConsultarEstadosAsesorInteractor;
import com.arquisoft.proyectos.infrastructure.estadoasesor.query.primaryadapter.web.dto.EstadoAsesorResponseDTO;
import com.arquisoft.proyectos.infrastructure.estadoasesor.query.primaryadapter.web.mapper.EstadoAsesorResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ProyectosApiMessages;
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
@RequestMapping("${rutas.proyectos.proyectos-grado.base:/proyectos-grado}")
@RequiredArgsConstructor
@Tag(name = ProyectosApiMessages.ConsultaEstadoAsesor.TAG_NAME,
        description = ProyectosApiMessages.ConsultaEstadoAsesor.TAG_DESCRIPTION)
public class ConsultarEstadosAsesorController {

    private final ConsultarEstadosAsesorInteractor consultarEstadosAsesorInteractor;

    @GetMapping("${rutas.proyectos.proyectos-grado.estados-asesor:/estados-asesor}")
    @Operation(
            summary = ProyectosApiMessages.ConsultaEstadoAsesor.CONSULTAR_SUMMARY,
            description = ProyectosApiMessages.ConsultaEstadoAsesor.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = ProyectosApiMessages.ConsultaEstadoAsesor.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EstadoAsesorResponseDTO.class)
                    )),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ProyectosApiMessages.Comun.RESP_401,
                    content = @Content),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ProyectosApiMessages.ConsultaEstadoAsesor.CONSULTAR_RESP_403,
                    content = @Content)
    })
    public ResponseEntity<List<EstadoAsesorResponseDTO>> consultarEstadosAsesor() {
        var estados = consultarEstadosAsesorInteractor.ejecutar();

        return ResponseEntity.ok(estados.stream()
                .map(EstadoAsesorResponseMapper::toResponse)
                .toList());
    }
}
