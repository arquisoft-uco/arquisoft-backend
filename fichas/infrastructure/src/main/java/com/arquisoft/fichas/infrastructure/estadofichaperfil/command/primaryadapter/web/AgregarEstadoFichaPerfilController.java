package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.primaryadapter.web;

import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.interactor.AgregarEstadoFichaPerfilInteractor;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.primaryadapter.web.dto.AgregarEstadoFichaPerfilRequestDTO;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.primaryadapter.web.dto.AgregarEstadoFichaPerfilResponseDTO;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.primaryadapter.web.mapper.AgregarEstadoFichaPerfilRequestMapper;
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

import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.EstadoFichaPerfil.TAG_NAME,
        description = FichasApiMessages.EstadoFichaPerfil.TAG_DESCRIPTION)
public class AgregarEstadoFichaPerfilController {

    private final AgregarEstadoFichaPerfilInteractor agregarEstadoFichaPerfilInteractor;

    @PostMapping("${rutas.fichas.fichas-perfil.estado-asesor:/{fichaPerfilId}/estados-ficha}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ESTADO_FICHA_PERFIL_ASESOR_CREATE)
    @Operation(
            summary = FichasApiMessages.EstadoFichaPerfil.AGREGAR_ASESOR_SUMMARY,
            description = FichasApiMessages.EstadoFichaPerfil.AGREGAR_ASESOR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses(value = {
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = FichasApiMessages.EstadoFichaPerfil.AGREGAR_ASESOR_RESP_201,
                    content = @Content(schema = @Schema(
                            implementation = AgregarEstadoFichaPerfilResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.EstadoFichaPerfil.AGREGAR_ASESOR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.EstadoFichaPerfil.AGREGAR_ASESOR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = FichasApiMessages.EstadoFichaPerfil.AGREGAR_ASESOR_RESP_422)
    })
    public ResponseEntity<AgregarEstadoFichaPerfilResponseDTO> agregar(
            @PathVariable UUID fichaPerfilId,
            @RequestBody AgregarEstadoFichaPerfilRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        var asesorFicha = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());
        var id = agregarEstadoFichaPerfilInteractor.ejecutar(
                AgregarEstadoFichaPerfilRequestMapper.toCommand(fichaPerfilId, request, asesorFicha));

        return ResponseEntity.status(HttpStatus.CREATED).body(new AgregarEstadoFichaPerfilResponseDTO(id));
    }
}
