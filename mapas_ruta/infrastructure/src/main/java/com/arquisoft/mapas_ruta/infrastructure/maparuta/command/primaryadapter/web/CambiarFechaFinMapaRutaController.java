package com.arquisoft.mapas_ruta.infrastructure.maparuta.command.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.interactor.CambiarFechaFinMapaRutaInteractor;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.primaryadapter.web.dto.CambiarFechaFinMapaRutaRequestDTO;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.primaryadapter.web.mapper.CambiarFechaFinMapaRutaRequestMapper;
import com.arquisoft.mapas_ruta.infrastructure.security.MapasRutaAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.MapasRutaApiMessages;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.mapas-ruta.mapas-ruta.base:/mapas-ruta}")
@RequiredArgsConstructor
@Tag(
        name = MapasRutaApiMessages.MapaRuta.TAG_NAME,
        description = MapasRutaApiMessages.MapaRuta.TAG_DESCRIPTION)
public class CambiarFechaFinMapaRutaController {

    private final CambiarFechaFinMapaRutaInteractor interactor;

    @PatchMapping("${rutas.mapas-ruta.mapas-ruta.fecha-fin:/{id}/fecha-fin}")
    @PreAuthorize(MapasRutaAuthorities.Expresiones.HAS_MAPA_RUTA_UPDATE_FECHA_FIN)
    @Operation(
            summary = MapasRutaApiMessages.MapaRuta.CAMBIAR_FECHA_FIN_SUMMARY,
            description = MapasRutaApiMessages.MapaRuta.CAMBIAR_FECHA_FIN_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.NO_CONTENT,
                    description = MapasRutaApiMessages.MapaRuta.CAMBIAR_FECHA_FIN_RESP_204),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = MapasRutaApiMessages.MapaRuta.CAMBIAR_FECHA_FIN_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = MapasRutaApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = MapasRutaApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = MapasRutaApiMessages.MapaRuta.CAMBIAR_FECHA_FIN_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> cambiarFechaFin(
            @PathVariable UUID id,
            @RequestBody CambiarFechaFinMapaRutaRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        var coordinador = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());
        interactor.ejecutar(CambiarFechaFinMapaRutaRequestMapper.toCommand(request, id, coordinador));
        return ResponseEntity.noContent().build();
    }
}
