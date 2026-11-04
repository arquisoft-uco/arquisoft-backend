package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.primaryadapter.web;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.interactor.DestituirEstudianteProyectoGradoInteractor;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.primaryadapter.web.mapper.DestituirEstudianteProyectoGradoRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ProyectosApiMessages;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.proyectos.proyectos-grado.base:/proyectos-grado}")
@RequiredArgsConstructor
@Tag(name = ProyectosApiMessages.EstudianteProyectoGrado.TAG_NAME,
        description = ProyectosApiMessages.EstudianteProyectoGrado.TAG_DESCRIPTION)
public class DestituirEstudianteProyectoGradoController {

    private final DestituirEstudianteProyectoGradoInteractor destituirEstudianteProyectoGradoInteractor;

    @DeleteMapping("${rutas.proyectos.proyectos-grado.estudiante:/{proyectoGradoId}/estudiantes/{estudianteId}}")
    @Operation(
            summary = ProyectosApiMessages.EstudianteProyectoGrado.DESTITUIR_SUMMARY,
            description = ProyectosApiMessages.EstudianteProyectoGrado.DESTITUIR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.NO_CONTENT,
                    description = ProyectosApiMessages.EstudianteProyectoGrado.DESTITUIR_RESP_204),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = ProyectosApiMessages.EstudianteProyectoGrado.DESTITUIR_RESP_400),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = ProyectosApiMessages.EstudianteProyectoGrado.DESTITUIR_RESP_422),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = ProyectosApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = ProyectosApiMessages.EstudianteProyectoGrado.DESTITUIR_RESP_403)
    })
    public ResponseEntity<Void> destituirEstudiante(
            @PathVariable UUID proyectoGradoId,
            @PathVariable UUID estudianteId,
            IdentidadActual identidad) {

        var coordinador = identidad.id();

        destituirEstudianteProyectoGradoInteractor.ejecutar(
                DestituirEstudianteProyectoGradoRequestMapper.toCommand(proyectoGradoId, estudianteId, coordinador));

        return ResponseEntity.noContent().build();
    }
}
