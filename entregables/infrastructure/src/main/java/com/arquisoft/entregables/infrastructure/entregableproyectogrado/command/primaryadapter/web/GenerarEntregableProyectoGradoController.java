package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.interactor.GenerarEntregableProyectoGradoInteractor;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web.dto.GenerarEntregableProyectoGradoResponseDTO;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web.mapper.GenerarEntregableProyectoGradoRequestMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EntregablesApiMessages;
import com.arquisoft.shared.web.identidad.IdentidadActual;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.entregables.proyectos-grado.base:/proyectos-grado}")
@RequiredArgsConstructor
@Tag(name = EntregablesApiMessages.EntregableProyectoGrado.TAG_NAME,
        description = EntregablesApiMessages.EntregableProyectoGrado.TAG_DESCRIPTION)
public class GenerarEntregableProyectoGradoController {

    private final GenerarEntregableProyectoGradoInteractor generarEntregableProyectoGradoInteractor;

    @PostMapping("${rutas.entregables.proyectos-grado.entregables:/{proyectoGradoId}/entregables}")
    @Operation(
            summary = EntregablesApiMessages.EntregableProyectoGrado.GENERAR_SUMMARY,
            description = EntregablesApiMessages.EntregableProyectoGrado.GENERAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.CREATED,
                    description = EntregablesApiMessages.EntregableProyectoGrado.GENERAR_RESP_201,
                    content = @Content(schema = @Schema(implementation = GenerarEntregableProyectoGradoResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = EntregablesApiMessages.EntregableProyectoGrado.GENERAR_RESP_400,
                    content = @Content),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = EntregablesApiMessages.Comun.RESP_401,
                    content = @Content),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = EntregablesApiMessages.Comun.RESP_403,
                    content = @Content),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = EntregablesApiMessages.EntregableProyectoGrado.GENERAR_RESP_422,
                    content = @Content)
    })
    public ResponseEntity<GenerarEntregableProyectoGradoResponseDTO> generarEntregable(
            @PathVariable UUID proyectoGradoId,
            IdentidadActual identidad) {

        var entregableId = generarEntregableProyectoGradoInteractor.ejecutar(
                GenerarEntregableProyectoGradoRequestMapper.toCommand(proyectoGradoId, identidad));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new GenerarEntregableProyectoGradoResponseDTO(entregableId));
    }
}
