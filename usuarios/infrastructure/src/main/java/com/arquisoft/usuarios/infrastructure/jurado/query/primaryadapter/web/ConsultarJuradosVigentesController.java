package com.arquisoft.usuarios.infrastructure.jurado.query.primaryadapter.web;

import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.UsuariosApiMessages;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.dto.PageResponseDTO;
import com.arquisoft.usuarios.application.jurado.query.primaryport.interactor.ConsultarJuradosVigentesInteractor;
import com.arquisoft.usuarios.infrastructure.jurado.query.primaryadapter.web.dto.JuradoVigenteResponseDTO;
import com.arquisoft.usuarios.infrastructure.jurado.query.primaryadapter.web.mapper.ConsultarJuradosVigentesRequestMapper;
import com.arquisoft.usuarios.infrastructure.jurado.query.primaryadapter.web.mapper.JuradoVigenteResponseMapper;
import com.arquisoft.usuarios.infrastructure.security.UsuariosAuthorities;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.usuarios.usuarios.base:/usuarios}")
@RequiredArgsConstructor
@Tag(name = UsuariosApiMessages.Usuario.TAG_NAME, description = UsuariosApiMessages.Usuario.TAG_DESCRIPTION)
public class ConsultarJuradosVigentesController {

    private final ConsultarJuradosVigentesInteractor consultarJuradosVigentesInteractor;

    @PostMapping("${rutas.usuarios.usuarios.jurados-vigentes:/jurados/vigentes}")
    @PreAuthorize(UsuariosAuthorities.Expresiones.HAS_JURADO_VIGENTE_VIEW)
    @Operation(
            summary = UsuariosApiMessages.Jurado.CONSULTAR_VIGENTES_SUMMARY,
            description = UsuariosApiMessages.Jurado.CONSULTAR_VIGENTES_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = UsuariosApiMessages.Jurado.CONSULTAR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = UsuariosApiMessages.Jurado.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = UsuariosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = UsuariosApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<PageResponseDTO<JuradoVigenteResponseDTO>> consultar(
            @RequestBody(required = false) QueryCriteriaRequestDTO request) {

        var resultado = consultarJuradosVigentesInteractor.ejecutar(
                ConsultarJuradosVigentesRequestMapper.toQuery(request));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(JuradoVigenteResponseMapper::toResponse)));
    }
}
