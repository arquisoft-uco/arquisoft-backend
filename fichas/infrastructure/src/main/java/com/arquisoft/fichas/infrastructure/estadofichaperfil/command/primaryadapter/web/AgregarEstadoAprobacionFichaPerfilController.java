package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.primaryadapter.web;

import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.interactor.AgregarEstadoAprobacionFichaPerfilInteractor;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.primaryadapter.web.dto.AgregarEstadoAprobacionFichaPerfilRequestDTO;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.primaryadapter.web.dto.AgregarEstadoAprobacionFichaPerfilResponseDTO;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.primaryadapter.web.mapper.AgregarEstadoAprobacionFichaPerfilRequestMapper;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.shared.util.UtilUUID;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.EstadoFichaPerfil.TAG_NAME,
        description = FichasApiMessages.EstadoFichaPerfil.TAG_DESCRIPTION)
public class AgregarEstadoAprobacionFichaPerfilController {

    private final AgregarEstadoAprobacionFichaPerfilInteractor agregarEstadoAprobacionFichaPerfilInteractor;

    @PostMapping("${rutas.fichas.fichas-perfil.estado-aprobacion:/{fichaPerfilId}/estados-ficha/aprobacion}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ESTADO_FICHA_PERFIL_APROBACION_CREATE)
    @Operation(
            summary = FichasApiMessages.EstadoFichaPerfil.AGREGAR_APROBACION_SUMMARY,
            description = FichasApiMessages.EstadoFichaPerfil.AGREGAR_APROBACION_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses(value = {
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = FichasApiMessages.EstadoFichaPerfil.AGREGAR_APROBACION_RESP_201,
                    content = @Content(schema = @Schema(
                            implementation = AgregarEstadoAprobacionFichaPerfilResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.EstadoFichaPerfil.AGREGAR_APROBACION_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.EstadoFichaPerfil.AGREGAR_APROBACION_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiMessages.EstadoFichaPerfil.AGREGAR_APROBACION_RESP_422)
    })
    public ResponseEntity<AgregarEstadoAprobacionFichaPerfilResponseDTO> agregar(
            @PathVariable String fichaPerfilId,
            @RequestBody AgregarEstadoAprobacionFichaPerfilRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        var coordinador = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());
        var id = agregarEstadoAprobacionFichaPerfilInteractor.ejecutar(
                AgregarEstadoAprobacionFichaPerfilRequestMapper.toCommand(fichaPerfilId, request, coordinador));

        return ResponseEntity.status(HttpStatus.CREATED).body(new AgregarEstadoAprobacionFichaPerfilResponseDTO(id));
    }
}
