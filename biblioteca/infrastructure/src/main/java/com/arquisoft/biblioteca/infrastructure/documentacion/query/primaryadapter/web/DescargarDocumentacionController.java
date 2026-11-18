package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web;

import com.arquisoft.biblioteca.application.documentacion.query.primaryport.interactor.DescargarDocumentacionInteractor;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.dto.EnlaceDescargaDocumentacionResponseDTO;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.mapper.DescargarDocumentacionRequestMapper;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.mapper.EnlaceDescargaDocumentacionResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.BibliotecaApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.biblioteca.biblioteca.base:/biblioteca}"
        + "${rutas.biblioteca.biblioteca.documentaciones:/documentaciones}")
@RequiredArgsConstructor
@Tag(name = BibliotecaApiMessages.Documentacion.TAG_NAME,
        description = BibliotecaApiMessages.Documentacion.TAG_DESCRIPTION)
public class DescargarDocumentacionController {

    private final DescargarDocumentacionInteractor descargarDocumentacionInteractor;

    @GetMapping("${rutas.biblioteca.biblioteca.descarga:/{documentacionId}/descarga}")
    @Operation(
            summary = BibliotecaApiMessages.Documentacion.DESCARGAR_SUMMARY,
            description = BibliotecaApiMessages.Documentacion.DESCARGAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = BibliotecaApiMessages.Documentacion.DESCARGAR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EnlaceDescargaDocumentacionResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = BibliotecaApiMessages.Documentacion.DESCARGAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = BibliotecaApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = BibliotecaApiMessages.Comun.RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = BibliotecaApiMessages.Documentacion.DESCARGAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.SERVICE_UNAVAILABLE,
                    description = BibliotecaApiMessages.Documentacion.DESCARGAR_RESP_503,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<EnlaceDescargaDocumentacionResponseDTO> descargarDocumentacion(
            @Parameter(description = BibliotecaApiMessages.Documentacion.PARAM_DOCUMENTACION)
            @PathVariable UUID documentacionId,
            IdentidadActual identidad) {

        var enlace = descargarDocumentacionInteractor.ejecutar(
                DescargarDocumentacionRequestMapper.toQuery(documentacionId, identidad));

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(EnlaceDescargaDocumentacionResponseMapper.toResponse(enlace));
    }
}
