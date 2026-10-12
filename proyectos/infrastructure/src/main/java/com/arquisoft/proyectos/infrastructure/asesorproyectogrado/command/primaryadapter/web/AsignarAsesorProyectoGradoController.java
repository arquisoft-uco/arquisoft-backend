package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.primaryadapter.web;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.interactor.AsignarAsesorProyectoGradoInteractor;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.primaryadapter.web.dto.AsignarAsesorProyectoGradoRequestDTO;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.primaryadapter.web.dto.AsignarAsesorProyectoGradoResponseDTO;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.primaryadapter.web.mapper.AsignarAsesorProyectoGradoRequestMapper;
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
@RequestMapping("${rutas.proyectos.proyectos-grado.base:/proyectos-grado}")
@RequiredArgsConstructor
@Tag(name = ProyectosApiMessages.AsesorProyectoGrado.TAG_NAME,
        description = ProyectosApiMessages.AsesorProyectoGrado.TAG_DESCRIPTION)
public class AsignarAsesorProyectoGradoController {

    private final AsignarAsesorProyectoGradoInteractor asignarAsesorProyectoGradoInteractor;

    @PostMapping("${rutas.proyectos.proyectos-grado.asesores:/{proyectoGradoId}/asesores}")
    @PreAuthorize(ProyectosAuthorities.Expresiones.HAS_ASESOR_PROYECTO_GRADO_CREATE)
    @Operation(
            summary = ProyectosApiMessages.AsesorProyectoGrado.ASIGNAR_SUMMARY,
            description = ProyectosApiMessages.AsesorProyectoGrado.ASIGNAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = ProyectosApiMessages.AsesorProyectoGrado.ASIGNAR_RESP_201),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ProyectosApiMessages.AsesorProyectoGrado.ASIGNAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = ProyectosApiMessages.AsesorProyectoGrado.ASIGNAR_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ProyectosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ProyectosApiMessages.AsesorProyectoGrado.ASIGNAR_RESP_403)
    })
    public ResponseEntity<AsignarAsesorProyectoGradoResponseDTO> asignarAsesor(
            @PathVariable UUID proyectoGradoId,
            @RequestBody AsignarAsesorProyectoGradoRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        var coordinador = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());

        var id = asignarAsesorProyectoGradoInteractor.ejecutar(
                AsignarAsesorProyectoGradoRequestMapper.toCommand(dto, proyectoGradoId, coordinador));

        return ResponseEntity.status(HttpStatus.CREATED).body(new AsignarAsesorProyectoGradoResponseDTO(id));
    }
}
