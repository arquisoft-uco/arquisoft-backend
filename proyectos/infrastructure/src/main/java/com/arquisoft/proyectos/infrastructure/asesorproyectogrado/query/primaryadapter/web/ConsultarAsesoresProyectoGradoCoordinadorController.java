package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.interactor.ConsultarAsesoresProyectoGradoCoordinadorInteractor;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web.dto.AsesorProyectoGradoResponseDTO;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web.mapper.ConsultarAsesoresProyectoGradoCoordinadorRequestMapper;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web.mapper.AsesorProyectoGradoResponseMapper;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${rutas.proyectos.proyectos-grado.base:/proyectos-grado}")
@RequiredArgsConstructor
@Tag(name = ProyectosApiMessages.ConsultaAsesorProyectoGrado.TAG_NAME,
        description = ProyectosApiMessages.ConsultaAsesorProyectoGrado.TAG_DESCRIPTION)
public class ConsultarAsesoresProyectoGradoCoordinadorController {

    private final ConsultarAsesoresProyectoGradoCoordinadorInteractor interactor;

    @GetMapping("${rutas.proyectos.proyectos-grado.asesores:/{proyectoGradoId}/asesores}")
    @Operation(
            summary = ProyectosApiMessages.ConsultaAsesorProyectoGrado.CONSULTAR_SUMMARY,
            description = ProyectosApiMessages.ConsultaAsesorProyectoGrado.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = ProyectosApiMessages.ConsultaAsesorProyectoGrado.CONSULTAR_RESP_200,
                    content = @Content(schema = @Schema(implementation = AsesorProyectoGradoResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ProyectosApiMessages.ConsultaAsesorProyectoGrado.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ProyectosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ProyectosApiMessages.ConsultaAsesorProyectoGrado.CONSULTAR_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = ProyectosApiMessages.ConsultaAsesorProyectoGrado.CONSULTAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<AsesorProyectoGradoResponseDTO>> consultarAsesores(
            @PathVariable UUID proyectoGradoId,
            IdentidadActual identidad) {

        var coordinador = identidad.id();

        var query = ConsultarAsesoresProyectoGradoCoordinadorRequestMapper.toQuery(proyectoGradoId, coordinador);

        return ResponseEntity.ok(interactor.ejecutar(query).stream()
                .map(AsesorProyectoGradoResponseMapper::toResponse)
                .toList());
    }
}
