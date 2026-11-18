package com.arquisoft.entregables.infrastructure.paqueteentregable.query.primaryadapter.web;

import com.arquisoft.entregables.application.paqueteentregable.query.primaryport.interactor.DescargarPaqueteEntregableInteractor;
import com.arquisoft.entregables.infrastructure.paqueteentregable.query.primaryadapter.web.dto.EnlaceDescargaPaqueteEntregableResponseDTO;
import com.arquisoft.entregables.infrastructure.paqueteentregable.query.primaryadapter.web.mapper.DescargarPaqueteEntregableRequestMapper;
import com.arquisoft.entregables.infrastructure.paqueteentregable.query.primaryadapter.web.mapper.EnlaceDescargaPaqueteEntregableResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EntregablesApiMessages;
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
@RequestMapping("${rutas.entregables.entregables.base:/entregables}")
@RequiredArgsConstructor
@Tag(name = EntregablesApiMessages.PaqueteEntregable.TAG_NAME,
        description = EntregablesApiMessages.PaqueteEntregable.TAG_DESCRIPTION)
public class DescargarPaqueteEntregableController {

    private final DescargarPaqueteEntregableInteractor descargarPaqueteEntregableInteractor;

    @GetMapping("${rutas.entregables.entregables.paquete:/{entregableProyectoGradoId}/paquete}")
    @Operation(
            summary = EntregablesApiMessages.PaqueteEntregable.DESCARGAR_SUMMARY,
            description = EntregablesApiMessages.PaqueteEntregable.DESCARGAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = EntregablesApiMessages.PaqueteEntregable.DESCARGAR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EnlaceDescargaPaqueteEntregableResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = EntregablesApiMessages.PaqueteEntregable.DESCARGAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = EntregablesApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = EntregablesApiMessages.Comun.RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = EntregablesApiMessages.PaqueteEntregable.DESCARGAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.SERVICE_UNAVAILABLE,
                    description = EntregablesApiMessages.PaqueteEntregable.DESCARGAR_RESP_503,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<EnlaceDescargaPaqueteEntregableResponseDTO> descargarPaqueteEntregable(
            @Parameter(description = EntregablesApiMessages.PaqueteEntregable.PARAM_ENTREGABLE)
            @PathVariable UUID entregableProyectoGradoId,
            IdentidadActual identidad) {

        var enlace = descargarPaqueteEntregableInteractor.ejecutar(
                DescargarPaqueteEntregableRequestMapper.toQuery(entregableProyectoGradoId, identidad));

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(EnlaceDescargaPaqueteEntregableResponseMapper.toResponse(enlace));
    }
}
