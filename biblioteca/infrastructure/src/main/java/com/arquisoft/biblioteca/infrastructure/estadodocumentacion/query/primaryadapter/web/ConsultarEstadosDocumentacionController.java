package com.arquisoft.biblioteca.infrastructure.estadodocumentacion.query.primaryadapter.web;

import com.arquisoft.biblioteca.application.estadodocumentacion.query.primaryport.interactor.ConsultarEstadosDocumentacionInteractor;
import com.arquisoft.biblioteca.infrastructure.estadodocumentacion.query.primaryadapter.web.dto.EstadoDocumentacionResponseDTO;
import com.arquisoft.biblioteca.infrastructure.estadodocumentacion.query.primaryadapter.web.mapper.EstadoDocumentacionResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.BibliotecaApiMessages;
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
@RequestMapping("${rutas.biblioteca.biblioteca.base:/biblioteca}")
@RequiredArgsConstructor
@Tag(name = BibliotecaApiMessages.EstadoDocumentacion.TAG_NAME,
        description = BibliotecaApiMessages.EstadoDocumentacion.TAG_DESCRIPTION)
public class ConsultarEstadosDocumentacionController {

    private final ConsultarEstadosDocumentacionInteractor consultarEstadosDocumentacionInteractor;

    @GetMapping("${rutas.biblioteca.biblioteca.estados-documentacion:/estados-documentacion}")
    @Operation(
            summary = BibliotecaApiMessages.EstadoDocumentacion.CONSULTAR_SUMMARY,
            description = BibliotecaApiMessages.EstadoDocumentacion.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = BibliotecaApiMessages.EstadoDocumentacion.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EstadoDocumentacionResponseDTO.class)
                    )),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = BibliotecaApiMessages.Comun.RESP_401,
                    content = @Content),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = BibliotecaApiMessages.Comun.RESP_403,
                    content = @Content)
    })
    public ResponseEntity<List<EstadoDocumentacionResponseDTO>> consultarEstadosDocumentacion() {
        var estados = consultarEstadosDocumentacionInteractor.ejecutar();

        return ResponseEntity.ok(estados.stream()
                .map(EstadoDocumentacionResponseMapper::toResponse)
                .toList());
    }
}
