package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.ConsultarCoordinadoresProyectoGradoEstudianteInteractor;
import com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.dto.CoordinadorProyectoGradoResponseDTO;
import com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper.ConsultarCoordinadoresProyectoGradoEstudianteRequestMapper;
import com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper.CoordinadorProyectoGradoResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ProyectosApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${rutas.proyectos.proyectos-grado.base:/proyectos-grado}")
@RequiredArgsConstructor
@Tag(name = ProyectosApiMessages.ConsultaProyectoGrado.TAG_NAME,
        description = ProyectosApiMessages.ConsultaProyectoGrado.TAG_DESCRIPTION)
public class ConsultarCoordinadoresProyectoGradoEstudianteController {

    private final ConsultarCoordinadoresProyectoGradoEstudianteInteractor interactor;

    @GetMapping("${rutas.proyectos.proyectos-grado.coordinadores:/estudiante/coordinadores}")
    @Operation(
            summary = ProyectosApiMessages.ConsultaProyectoGrado.CONSULTAR_COORDINADORES_SUMMARY,
            description = ProyectosApiMessages.ConsultaProyectoGrado.CONSULTAR_COORDINADORES_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = ProyectosApiMessages.ConsultaProyectoGrado.CONSULTAR_COORDINADORES_RESP_200,
                    content = @Content(schema = @Schema(implementation = CoordinadorProyectoGradoResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ProyectosApiMessages.ConsultaProyectoGrado.CONSULTAR_COORDINADORES_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ProyectosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ProyectosApiMessages.ConsultaProyectoGrado.CONSULTAR_COORDINADORES_RESP_403)
    })
    public ResponseEntity<List<CoordinadorProyectoGradoResponseDTO>> consultarCoordinadores(
            IdentidadActual identidad) {

        var estudiante = identidad.id();

        var query = ConsultarCoordinadoresProyectoGradoEstudianteRequestMapper.toQuery(estudiante);

        return ResponseEntity.ok(interactor.ejecutar(query).stream()
                .map(CoordinadorProyectoGradoResponseMapper::toResponse)
                .toList());
    }
}
