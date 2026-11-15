package com.arquisoft.biblioteca.infrastructure.documentacion.command.primaryadapter.web;

import com.arquisoft.biblioteca.application.documentacion.command.primaryport.interactor.CambiarEstadoDocumentacionInteractor;
import com.arquisoft.biblioteca.infrastructure.documentacion.command.primaryadapter.web.dto.CambiarEstadoDocumentacionRequestDTO;
import com.arquisoft.biblioteca.infrastructure.documentacion.command.primaryadapter.web.mapper.CambiarEstadoDocumentacionRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.BibliotecaApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.biblioteca.biblioteca.base:/biblioteca}"
        + "${rutas.biblioteca.biblioteca.documentaciones:/documentaciones}")
@RequiredArgsConstructor
@Tag(name = BibliotecaApiMessages.Documentacion.TAG_NAME,
        description = BibliotecaApiMessages.Documentacion.TAG_DESCRIPTION)
public class CambiarEstadoDocumentacionController {

    private final CambiarEstadoDocumentacionInteractor cambiarEstadoDocumentacionInteractor;

    @PatchMapping("${rutas.biblioteca.biblioteca.estado:/{documentacionId}/estado}")
    @Operation(
            summary = BibliotecaApiMessages.Documentacion.CAMBIAR_ESTADO_SUMMARY,
            description = BibliotecaApiMessages.Documentacion.CAMBIAR_ESTADO_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = BibliotecaApiMessages.Documentacion.CAMBIAR_ESTADO_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = BibliotecaApiMessages.Documentacion.CAMBIAR_ESTADO_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = BibliotecaApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = BibliotecaApiMessages.Comun.RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = BibliotecaApiMessages.Documentacion.CAMBIAR_ESTADO_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> cambiarEstado(
            @Parameter(description = BibliotecaApiMessages.Documentacion.PARAM_DOCUMENTACION)
            @PathVariable UUID documentacionId,
            @RequestBody CambiarEstadoDocumentacionRequestDTO request) {

        cambiarEstadoDocumentacionInteractor.ejecutar(
                CambiarEstadoDocumentacionRequestMapper.toCommand(documentacionId, request));
        return ResponseEntity.noContent().build();
    }
}
