package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.ConsultarProyectosGradoEstudianteInteractor;
import com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.dto.ProyectoGradoResponseDTO;
import com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper.ConsultarProyectosGradoEstudianteRequestMapper;
import com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper.ProyectoGradoResponseMapper;
import com.arquisoft.proyectos.infrastructure.security.ProyectosAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ProyectosApiMessages;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${rutas.proyectos.proyectos-grado.base:/proyectos-grado}")
@RequiredArgsConstructor
@Tag(name = ProyectosApiMessages.ConsultaProyectoGrado.TAG_NAME,
        description = ProyectosApiMessages.ConsultaProyectoGrado.TAG_DESCRIPTION)
public class ConsultarProyectosGradoEstudianteController {

    private final ConsultarProyectosGradoEstudianteInteractor interactor;

    @GetMapping("${rutas.proyectos.proyectos-grado.proyectos-estudiante:/estudiante}")
    @PreAuthorize(ProyectosAuthorities.Expresiones.HAS_PROYECTO_GRADO_ESTUDIANTE_VIEW)
    @Operation(
            summary = ProyectosApiMessages.ConsultaProyectoGrado.CONSULTAR_ESTUDIANTE_SUMMARY,
            description = ProyectosApiMessages.ConsultaProyectoGrado.CONSULTAR_ESTUDIANTE_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = ProyectosApiMessages.ConsultaProyectoGrado.CONSULTAR_ESTUDIANTE_RESP_200,
                    content = @Content(schema = @Schema(implementation = ProyectoGradoResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ProyectosApiMessages.ConsultaProyectoGrado.CONSULTAR_ESTUDIANTE_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ProyectosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ProyectosApiMessages.ConsultaProyectoGrado.CONSULTAR_ESTUDIANTE_RESP_403)
    })
    public ResponseEntity<List<ProyectoGradoResponseDTO>> consultarProyectosGradoEstudiante(
            @AuthenticationPrincipal Jwt jwt) {

        var estudiante = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());

        var query = ConsultarProyectosGradoEstudianteRequestMapper.toQuery(estudiante);

        return ResponseEntity.ok(interactor.ejecutar(query).stream()
                .map(ProyectoGradoResponseMapper::toResponse)
                .toList());
    }
}
