package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.primaryadapter.web;

import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.interactor.ConsultarEntregablesProyectoGradoEstudianteInteractor;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.primaryadapter.web.dto.EntregableProyectoGradoResponseDTO;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.primaryadapter.web.mapper.ConsultarEntregablesProyectoGradoEstudianteRequestMapper;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.primaryadapter.web.mapper.EntregableProyectoGradoResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EntregablesApiMessages;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.dto.PageResponseDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.entregables.proyectos-grado.base:/proyectos-grado}")
@RequiredArgsConstructor
@Tag(name = EntregablesApiMessages.EntregableProyectoGrado.TAG_NAME,
        description = EntregablesApiMessages.EntregableProyectoGrado.TAG_DESCRIPTION)
public class ConsultarEntregablesProyectoGradoEstudianteController {

    private final ConsultarEntregablesProyectoGradoEstudianteInteractor consultarEntregablesProyectoGradoEstudianteInteractor;

    @PostMapping("${rutas.entregables.proyectos-grado.entregables-estudiante:/{proyectoGradoId}/entregables/estudiante}")
    @Operation(
            summary = EntregablesApiMessages.EntregableProyectoGrado.CONSULTAR_ESTUDIANTE_SUMMARY,
            description = EntregablesApiMessages.EntregableProyectoGrado.CONSULTAR_ESTUDIANTE_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = EntregablesApiMessages.EntregableProyectoGrado.CONSULTAR_ESTUDIANTE_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = EntregablesApiMessages.EntregableProyectoGrado.CONSULTAR_ESTUDIANTE_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = EntregablesApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = EntregablesApiMessages.Comun.RESP_403),
            @ApiResponse(responseCode = ApiCodes.UNPROCESSABLE,
                    description = EntregablesApiMessages.EntregableProyectoGrado.CONSULTAR_ESTUDIANTE_RESP_422,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<PageResponseDTO<EntregableProyectoGradoResponseDTO>> consultarEntregablesEstudiante(
            @PathVariable UUID proyectoGradoId,
            @RequestBody(required = false) QueryCriteriaRequestDTO request,
            IdentidadActual identidad) {

        var resultado = consultarEntregablesProyectoGradoEstudianteInteractor.ejecutar(
                ConsultarEntregablesProyectoGradoEstudianteRequestMapper.toQuery(request, proyectoGradoId, identidad));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(EntregableProyectoGradoResponseMapper::toResponse)));
    }
}
