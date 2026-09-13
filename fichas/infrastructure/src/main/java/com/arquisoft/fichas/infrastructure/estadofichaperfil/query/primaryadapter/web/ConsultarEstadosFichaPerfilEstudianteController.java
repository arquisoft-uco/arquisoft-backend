package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.interactor.ConsultarEstadosFichaPerfilEstudianteInteractor;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web.dto.EstadoFichaPerfilResponseDTO;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web.mapper.ConsultarEstadosFichaPerfilEstudianteRequestMapper;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web.mapper.EstadoFichaPerfilResponseMapper;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.EstadoFichaPerfil.TAG_NAME,
        description = FichasApiMessages.EstadoFichaPerfil.TAG_DESCRIPTION)
public class ConsultarEstadosFichaPerfilEstudianteController {

    private final ConsultarEstadosFichaPerfilEstudianteInteractor consultarEstadosFichaPerfilEstudianteInteractor;

    @GetMapping("${rutas.fichas.fichas-perfil.estados-estudiante:/{fichaPerfilId}/estados-ficha/estudiante}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ESTADO_FICHA_PERFIL_ESTUDIANTE_VIEW)
    @Operation(
            summary = FichasApiMessages.EstadoFichaPerfil.CONSULTAR_ESTUDIANTE_SUMMARY,
            description = FichasApiMessages.EstadoFichaPerfil.CONSULTAR_ESTUDIANTE_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = FichasApiMessages.EstadoFichaPerfil.CONSULTAR_ESTUDIANTE_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EstadoFichaPerfilResponseDTO.class)))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.EstadoFichaPerfil.CONSULTAR_ESTUDIANTE_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.EstadoFichaPerfil.CONSULTAR_ESTUDIANTE_RESP_403)
    })
    public ResponseEntity<List<EstadoFichaPerfilResponseDTO>> consultarEstados(
            @PathVariable UUID fichaPerfilId,
            @AuthenticationPrincipal Jwt jwt) {

        var estudiante = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());

        var estados = consultarEstadosFichaPerfilEstudianteInteractor.ejecutar(
                ConsultarEstadosFichaPerfilEstudianteRequestMapper.toQuery(fichaPerfilId, estudiante));

        return ResponseEntity.ok(estados.stream()
                .map(EstadoFichaPerfilResponseMapper::toResponse)
                .toList());
    }
}
