package com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.primaryadapter.web;

import com.arquisoft.biblioteca.application.autordocumentacion.query.primaryport.interactor.ConsultarAutoresDocumentacionInteractor;
import com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.primaryadapter.web.dto.AutorDocumentacionResponseDTO;
import com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.primaryadapter.web.mapper.AutorDocumentacionResponseMapper;
import com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.primaryadapter.web.mapper.ConsultarAutoresDocumentacionRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.BibliotecaApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${rutas.biblioteca.biblioteca.base:/biblioteca}"
        + "${rutas.biblioteca.biblioteca.documentaciones:/documentaciones}")
@RequiredArgsConstructor
@Tag(name = BibliotecaApiMessages.AutorDocumentacion.TAG_NAME,
        description = BibliotecaApiMessages.AutorDocumentacion.TAG_DESCRIPTION)
public class ConsultarAutoresDocumentacionController {

    private final ConsultarAutoresDocumentacionInteractor consultarAutoresDocumentacionInteractor;

    @GetMapping("${rutas.biblioteca.biblioteca.autores:/{documentacionId}/autores}")
    @Operation(
            summary = BibliotecaApiMessages.AutorDocumentacion.CONSULTAR_SUMMARY,
            description = BibliotecaApiMessages.AutorDocumentacion.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = BibliotecaApiMessages.AutorDocumentacion.CONSULTAR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AutorDocumentacionResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = BibliotecaApiMessages.AutorDocumentacion.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = BibliotecaApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = BibliotecaApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<List<AutorDocumentacionResponseDTO>> consultarAutoresDocumentacion(
            @PathVariable("documentacionId") UUID documentacionId) {

        var autores = consultarAutoresDocumentacionInteractor.ejecutar(
                ConsultarAutoresDocumentacionRequestMapper.toQuery(documentacionId));

        return ResponseEntity.ok(autores.stream()
                .map(AutorDocumentacionResponseMapper::toResponse)
                .toList());
    }
}
