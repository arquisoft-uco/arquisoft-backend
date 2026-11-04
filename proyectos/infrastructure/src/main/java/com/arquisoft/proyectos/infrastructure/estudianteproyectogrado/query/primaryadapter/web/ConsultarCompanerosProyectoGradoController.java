package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.interactor.ConsultarCompanerosProyectoGradoInteractor;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.dto.CompaneroProyectoGradoResponseDTO;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.mapper.CompaneroProyectoGradoResponseMapper;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.mapper.ConsultarCompanerosProyectoGradoRequestMapper;
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
@Tag(name = ProyectosApiMessages.ConsultaEstudianteProyectoGrado.TAG_NAME,
        description = ProyectosApiMessages.ConsultaEstudianteProyectoGrado.TAG_DESCRIPTION)
public class ConsultarCompanerosProyectoGradoController {

    private final ConsultarCompanerosProyectoGradoInteractor interactor;

    @GetMapping("${rutas.proyectos.proyectos-grado.companeros:/{proyectoGradoId}/companeros}")
    @Operation(
            summary = ProyectosApiMessages.ConsultaEstudianteProyectoGrado.CONSULTAR_COMPANEROS_SUMMARY,
            description = ProyectosApiMessages.ConsultaEstudianteProyectoGrado.CONSULTAR_COMPANEROS_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = ProyectosApiMessages.ConsultaEstudianteProyectoGrado.CONSULTAR_COMPANEROS_RESP_200,
                    content = @Content(schema = @Schema(implementation = CompaneroProyectoGradoResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ProyectosApiMessages.ConsultaEstudianteProyectoGrado.CONSULTAR_COMPANEROS_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ProyectosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ProyectosApiMessages.ConsultaEstudianteProyectoGrado.CONSULTAR_COMPANEROS_RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = ProyectosApiMessages.ConsultaEstudianteProyectoGrado.CONSULTAR_COMPANEROS_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<CompaneroProyectoGradoResponseDTO>> consultarCompaneros(
            @PathVariable UUID proyectoGradoId,
            IdentidadActual identidad) {

        var estudiante = identidad.id();

        var query = ConsultarCompanerosProyectoGradoRequestMapper.toQuery(proyectoGradoId, estudiante);

        return ResponseEntity.ok(interactor.ejecutar(query).stream()
                .map(CompaneroProyectoGradoResponseMapper::toResponse)
                .toList());
    }
}
