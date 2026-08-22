package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web;

import com.arquisoft.seguridad.application.auth.command.primaryport.interactor.ValidarTokenInteractor;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto.ValidarTokenResponseDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.mapper.ValidarTokenRequestMapper;
import com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.mapper.ValidarTokenResponseMapper;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.seguridad.auth.base:/auth}")
@RequiredArgsConstructor
@Tag(name = SeguridadApiMessages.Autenticacion.TAG_NAME,
        description = SeguridadApiMessages.Autenticacion.TAG_DESCRIPTION)
public class ValidarTokenController {

    private final ValidarTokenInteractor validarTokenInteractor;

    @PostMapping("${rutas.seguridad.auth.validate:/validate}")
    @Operation(
            summary = SeguridadApiMessages.Autenticacion.VALIDAR_SUMMARY,
            description = SeguridadApiMessages.Autenticacion.VALIDAR_DESCRIPTION
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = SeguridadApiMessages.Autenticacion.VALIDAR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ValidarTokenResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = SeguridadApiMessages.Autenticacion.VALIDAR_RESP_400,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<ValidarTokenResponseDTO> ejecutar(@RequestParam String token) {
        var resultado = validarTokenInteractor.ejecutar(ValidarTokenRequestMapper.toCommand(token));
        return ResponseEntity.ok(ValidarTokenResponseMapper.toResponse(resultado));
    }
}
