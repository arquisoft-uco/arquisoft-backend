package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.interactor.ConsultarMapaRutaProyectoGradoAsesorInteractor;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.dto.MapaRutaEstudianteResponseDTO;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.mapper.ConsultarMapaRutaProyectoGradoAsesorRequestMapper;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.mapper.MapaRutaEstudianteResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.MapasRutaApiMessages;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.mapas-ruta.mapas-ruta.base:/mapas-ruta}")
@RequiredArgsConstructor
@Tag(
        name = MapasRutaApiMessages.MapaRuta.TAG_NAME,
        description = MapasRutaApiMessages.MapaRuta.TAG_DESCRIPTION)
public class ConsultarMapaRutaProyectoGradoAsesorController {

    private final ConsultarMapaRutaProyectoGradoAsesorInteractor consultarMapaRutaProyectoGradoAsesorInteractor;

    @GetMapping("${rutas.mapas-ruta.mapas-ruta.proyecto-asesor:/proyectos-grado/{proyectoGradoId}/asesor}")
    @Operation(
            summary = MapasRutaApiMessages.MapaRuta.CONSULTAR_PROYECTO_ASESOR_SUMMARY,
            description = MapasRutaApiMessages.MapaRuta.CONSULTAR_PROYECTO_ASESOR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = MapasRutaApiMessages.MapaRuta.CONSULTAR_PROYECTO_ASESOR_RESP_200,
                    content = @Content(schema = @Schema(implementation = MapaRutaEstudianteResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = MapasRutaApiMessages.MapaRuta.CONSULTAR_PROYECTO_ASESOR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = MapasRutaApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = MapasRutaApiMessages.Comun.RESP_403),
            @ApiResponse(
                    responseCode = ApiCodes.NOT_FOUND,
                    description = MapasRutaApiMessages.MapaRuta.CONSULTAR_PROYECTO_ASESOR_RESP_404)
    })
    public ResponseEntity<MapaRutaEstudianteResponseDTO> consultarMapaRuta(@PathVariable UUID proyectoGradoId) {
        return consultarMapaRutaProyectoGradoAsesorInteractor
                .ejecutar(ConsultarMapaRutaProyectoGradoAsesorRequestMapper.toQuery(proyectoGradoId))
                .map(MapaRutaEstudianteResponseMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
