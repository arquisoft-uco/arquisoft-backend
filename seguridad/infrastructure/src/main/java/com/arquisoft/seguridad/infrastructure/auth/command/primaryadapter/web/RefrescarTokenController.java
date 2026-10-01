package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web;

import com.arquisoft.seguridad.application.auth.command.primaryport.interactor.RefrescarTokenInteractor;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto.RefrescarTokenRequestDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto.RefrescarTokenResponseDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.mapper.RefrescarTokenRequestMapper;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.mapper.RefrescarTokenResponseMapper;
import com.arquisoft.shared.message.annotation.SeguridadApiMessages;
import com.arquisoft.shared.message.annotation.ApiCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.seguridad.auth.base:/auth}")
@RequiredArgsConstructor
@Tag(name = SeguridadApiMessages.Autenticacion.TAG_NAME,
        description = SeguridadApiMessages.Autenticacion.TAG_DESCRIPTION)
public class RefrescarTokenController {

    private final RefrescarTokenInteractor refrescarTokenInteractor;

    @PostMapping("${rutas.seguridad.auth.refresh:/refresh}")
    @Operation(
            summary = SeguridadApiMessages.Autenticacion.REFRESCAR_SUMMARY,
            description = SeguridadApiMessages.Autenticacion.REFRESCAR_DESCRIPTION
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = SeguridadApiMessages.Autenticacion.REFRESCAR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RefrescarTokenResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = SeguridadApiMessages.Autenticacion.REFRESCAR_RESP_400,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = SeguridadApiMessages.Autenticacion.REFRESCAR_RESP_401,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<RefrescarTokenResponseDTO> ejecutar(
            @RequestBody RefrescarTokenRequestDTO solicitud) {

        var resultado = refrescarTokenInteractor.ejecutar(RefrescarTokenRequestMapper.toCommand(solicitud));
        return ResponseEntity.ok(RefrescarTokenResponseMapper.toResponse(resultado));
    }
}
