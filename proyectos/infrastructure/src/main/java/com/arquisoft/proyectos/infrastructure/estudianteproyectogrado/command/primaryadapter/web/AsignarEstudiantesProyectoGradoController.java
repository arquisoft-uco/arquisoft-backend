package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.primaryadapter.web;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.interactor.AsignarEstudiantesProyectoGradoInteractor;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.primaryadapter.web.dto.AsignarEstudiantesProyectoGradoRequestDTO;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.primaryadapter.web.mapper.AsignarEstudiantesProyectoGradoRequestMapper;
import com.arquisoft.proyectos.infrastructure.security.ProyectosAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ProyectosApiMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.proyectos.proyectos-grado.base:/proyectos/proyectos-grado}")
@RequiredArgsConstructor
@Tag(name = ProyectosApiMessages.EstudianteProyectoGrado.TAG_NAME,
        description = ProyectosApiMessages.EstudianteProyectoGrado.TAG_DESCRIPTION)
public class AsignarEstudiantesProyectoGradoController {

    private final AsignarEstudiantesProyectoGradoInteractor asignarEstudiantesProyectoGradoInteractor;

    @PostMapping("${rutas.proyectos.proyectos-grado.estudiantes:/{proyectoGradoId}/estudiantes}")
    @PreAuthorize(ProyectosAuthorities.Expresiones.HAS_ESTUDIANTE_PROYECTO_GRADO_CREATE)
    @Operation(
            summary = ProyectosApiMessages.EstudianteProyectoGrado.ASIGNAR_SUMMARY,
            description = ProyectosApiMessages.EstudianteProyectoGrado.ASIGNAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = ProyectosApiMessages.EstudianteProyectoGrado.ASIGNAR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ProyectosApiMessages.EstudianteProyectoGrado.ASIGNAR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = ProyectosApiMessages.EstudianteProyectoGrado.ASIGNAR_RESP_422),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ProyectosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ProyectosApiMessages.EstudianteProyectoGrado.ASIGNAR_RESP_403)
    })
    public ResponseEntity<Void> asignarEstudiantes(
            @PathVariable UUID proyectoGradoId,
            @RequestBody AsignarEstudiantesProyectoGradoRequestDTO dto) {

        asignarEstudiantesProyectoGradoInteractor.ejecutar(
                AsignarEstudiantesProyectoGradoRequestMapper.toCommand(dto, proyectoGradoId));

        return ResponseEntity.noContent().build();
    }
}
