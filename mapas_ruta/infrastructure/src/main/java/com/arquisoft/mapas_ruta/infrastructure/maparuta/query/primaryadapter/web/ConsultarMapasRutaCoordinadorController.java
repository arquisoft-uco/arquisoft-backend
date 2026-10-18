package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.interactor.ConsultarMapasRutaCoordinadorInteractor;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.dto.MapaRutaResponseDTO;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.mapper.ConsultarMapasRutaCoordinadorRequestMapper;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.mapper.MapaRutaResponseMapper;
import com.arquisoft.mapas_ruta.infrastructure.security.MapasRutaAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.MapasRutaApiMessages;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.dto.PageResponseDTO;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.mapas-ruta.mapas-ruta.base:/mapas-ruta}")
@RequiredArgsConstructor
@Tag(name = MapasRutaApiMessages.MapaRuta.TAG_NAME, description = MapasRutaApiMessages.MapaRuta.TAG_DESCRIPTION)
public class ConsultarMapasRutaCoordinadorController {

    private final ConsultarMapasRutaCoordinadorInteractor consultarMapasRutaCoordinadorInteractor;

    @PostMapping("${rutas.mapas-ruta.mapas-ruta.coordinador:/coordinador}")
    @PreAuthorize(MapasRutaAuthorities.Expresiones.HAS_MAPA_RUTA_COORDINADOR_VIEW)
    @Operation(
            summary = MapasRutaApiMessages.MapaRuta.CONSULTAR_COORDINADOR_SUMMARY,
            description = MapasRutaApiMessages.MapaRuta.CONSULTAR_COORDINADOR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = MapasRutaApiMessages.MapaRuta.CONSULTAR_COORDINADOR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = MapasRutaApiMessages.MapaRuta.CONSULTAR_COORDINADOR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = MapasRutaApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = MapasRutaApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<PageResponseDTO<MapaRutaResponseDTO>> consultarMapasRuta(
            @RequestBody(required = false) QueryCriteriaRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        var coordinador = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());
        var resultado = consultarMapasRutaCoordinadorInteractor.ejecutar(
                ConsultarMapasRutaCoordinadorRequestMapper.toQuery(request, coordinador));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(MapaRutaResponseMapper::toResponse)));
    }
}
