package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web;

import com.arquisoft.biblioteca.application.documentacion.query.primaryport.interactor.ConsultarDocumentacionesInteractor;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.dto.DocumentacionResponseDTO;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.mapper.ConsultarDocumentacionesRequestMapper;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.mapper.DocumentacionResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.BibliotecaApiMessages;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.dto.PageResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.biblioteca.biblioteca.base:/biblioteca}"
        + "${rutas.biblioteca.biblioteca.documentaciones:/documentaciones}")
@RequiredArgsConstructor
@Tag(name = BibliotecaApiMessages.Documentacion.TAG_NAME,
        description = BibliotecaApiMessages.Documentacion.TAG_DESCRIPTION)
public class ConsultarDocumentacionesController {

    private final ConsultarDocumentacionesInteractor consultarDocumentacionesInteractor;

    @PostMapping("${rutas.biblioteca.biblioteca.consultar:/consultar}")
    @Operation(
            summary = BibliotecaApiMessages.Documentacion.CONSULTAR_SUMMARY,
            description = BibliotecaApiMessages.Documentacion.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = BibliotecaApiMessages.Documentacion.CONSULTAR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = BibliotecaApiMessages.Documentacion.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = BibliotecaApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = BibliotecaApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<PageResponseDTO<DocumentacionResponseDTO>> consultarDocumentaciones(
            @RequestBody(required = false) QueryCriteriaRequestDTO request,
            IdentidadActual identidad) {

        var resultado = consultarDocumentacionesInteractor.ejecutar(
                ConsultarDocumentacionesRequestMapper.toQuery(request, identidad));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(DocumentacionResponseMapper::toResponse)));
    }
}
