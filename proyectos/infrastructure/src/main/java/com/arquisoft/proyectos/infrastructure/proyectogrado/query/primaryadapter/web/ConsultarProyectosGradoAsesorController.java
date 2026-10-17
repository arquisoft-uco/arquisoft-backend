package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.ConsultarProyectosGradoAsesorInteractor;
import com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.dto.ProyectoGradoResponseDTO;
import com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper.ConsultarProyectosGradoAsesorRequestMapper;
import com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper.ProyectoGradoResponseMapper;
import com.arquisoft.proyectos.infrastructure.security.ProyectosAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ProyectosApiMessages;
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
@RequestMapping("${rutas.proyectos.proyectos-grado.base:/proyectos-grado}")
@RequiredArgsConstructor
@Tag(name = ProyectosApiMessages.ConsultaProyectoGrado.TAG_NAME,
        description = ProyectosApiMessages.ConsultaProyectoGrado.TAG_DESCRIPTION)
public class ConsultarProyectosGradoAsesorController {

    private final ConsultarProyectosGradoAsesorInteractor interactor;

    @PostMapping("${rutas.proyectos.proyectos-grado.proyectos-asesor:/asesor}")
    @PreAuthorize(ProyectosAuthorities.Expresiones.HAS_PROYECTO_GRADO_ASESOR_VIEW)
    @Operation(
            summary = ProyectosApiMessages.ConsultaProyectoGrado.CONSULTAR_ASESOR_SUMMARY,
            description = ProyectosApiMessages.ConsultaProyectoGrado.CONSULTAR_ASESOR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = ProyectosApiMessages.ConsultaProyectoGrado.CONSULTAR_ASESOR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ProyectosApiMessages.ConsultaProyectoGrado.CONSULTAR_ASESOR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ProyectosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ProyectosApiMessages.ConsultaProyectoGrado.CONSULTAR_ASESOR_RESP_403)
    })
    public ResponseEntity<PageResponseDTO<ProyectoGradoResponseDTO>> consultarProyectosGradoAsesor(
            @RequestBody(required = false) QueryCriteriaRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        var asesor = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());
        var resultado = interactor.ejecutar(
                ConsultarProyectosGradoAsesorRequestMapper.toQuery(request, asesor));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(ProyectoGradoResponseMapper::toResponse)));
    }
}
