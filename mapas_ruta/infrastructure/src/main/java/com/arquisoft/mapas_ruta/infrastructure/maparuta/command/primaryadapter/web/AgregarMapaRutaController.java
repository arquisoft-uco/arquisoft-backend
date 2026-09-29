package com.arquisoft.mapas_ruta.infrastructure.maparuta.command.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.interactor.AgregarMapaRutaInteractor;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.primaryadapter.web.dto.AgregarMapaRutaRequestDTO;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.primaryadapter.web.dto.AgregarMapaRutaResponseDTO;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.primaryadapter.web.mapper.AgregarMapaRutaRequestMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.mapas-ruta.mapas-ruta.base:/mapas-ruta}")
@RequiredArgsConstructor
@Tag(
        name = MapasRutaApiMessages.MapaRuta.TAG_NAME,
        description = MapasRutaApiMessages.MapaRuta.TAG_DESCRIPTION)
public class AgregarMapaRutaController {

    private final AgregarMapaRutaInteractor interactor;

    @PostMapping
    @PreAuthorize(MapasRutaAuthorities.Expresiones.HAS_MAPA_RUTA_CREATE)
    @Operation(
            summary = MapasRutaApiMessages.MapaRuta.AGREGAR_SUMMARY,
            description = MapasRutaApiMessages.MapaRuta.AGREGAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.CREATED,
                    description = MapasRutaApiMessages.MapaRuta.AGREGAR_RESP_201,
                    content = @Content(schema = @Schema(implementation = AgregarMapaRutaResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = MapasRutaApiMessages.MapaRuta.AGREGAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = MapasRutaApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = MapasRutaApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.UNPROCESSABLE,
                    description = MapasRutaApiMessages.MapaRuta.AGREGAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<AgregarMapaRutaResponseDTO> agregar(
            @RequestBody AgregarMapaRutaRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {
        var coordinador = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());
        var id = interactor.ejecutar(AgregarMapaRutaRequestMapper.toCommand(request, coordinador));
        return ResponseEntity.status(HttpStatus.CREATED).body(new AgregarMapaRutaResponseDTO(id));
    }
}
