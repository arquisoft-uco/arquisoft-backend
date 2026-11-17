package com.arquisoft.biblioteca.infrastructure.documentacion.command.primaryadapter.web;

import com.arquisoft.biblioteca.application.documentacion.command.primaryport.interactor.RegistrarDocumentacionInteractor;
import com.arquisoft.biblioteca.infrastructure.documentacion.command.primaryadapter.web.dto.RegistrarDocumentacionRequestDTO;
import com.arquisoft.biblioteca.infrastructure.documentacion.command.primaryadapter.web.dto.RegistrarDocumentacionResponseDTO;
import com.arquisoft.biblioteca.infrastructure.documentacion.command.primaryadapter.web.mapper.RegistrarDocumentacionRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.BibliotecaApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
public class RegistrarDocumentacionController {

    private final RegistrarDocumentacionInteractor registrarDocumentacionInteractor;

    @PostMapping
    @Operation(
            summary = BibliotecaApiMessages.Documentacion.REGISTRAR_SUMMARY,
            description = BibliotecaApiMessages.Documentacion.REGISTRAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = BibliotecaApiMessages.Documentacion.REGISTRAR_RESP_201),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = BibliotecaApiMessages.Documentacion.REGISTRAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = BibliotecaApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = BibliotecaApiMessages.Comun.RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = BibliotecaApiMessages.Documentacion.REGISTRAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<RegistrarDocumentacionResponseDTO> registrar(
            @RequestBody RegistrarDocumentacionRequestDTO request,
            IdentidadActual identidad) {
        var id = registrarDocumentacionInteractor.ejecutar(
                RegistrarDocumentacionRequestMapper.toCommand(request, identidad.id()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegistrarDocumentacionResponseDTO(id));
    }
}
