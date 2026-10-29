package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.primaryadapter.web;

import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.interactor.ConsultarEntregablesProyectoGradoAsesorInteractor;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.primaryadapter.web.dto.EntregableProyectoGradoResponseDTO;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.primaryadapter.web.mapper.ConsultarEntregablesProyectoGradoAsesorRequestMapper;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.entregables.entregables-proyecto-grado.base:/entregables-proyecto-grado}")
@RequiredArgsConstructor
@Tag(name = EntregablesApiMessages.EntregableProyectoGrado.TAG_NAME,
        description = EntregablesApiMessages.EntregableProyectoGrado.TAG_DESCRIPTION)
public class ConsultarEntregablesProyectoGradoAsesorController {

    private final ConsultarEntregablesProyectoGradoAsesorInteractor consultarEntregablesProyectoGradoAsesorInteractor;

    @PostMapping("${rutas.entregables.entregables-proyecto-grado.asesor:/asesor}")
    @Operation(
            summary = EntregablesApiMessages.EntregableProyectoGrado.CONSULTAR_ASESOR_SUMMARY,
            description = EntregablesApiMessages.EntregableProyectoGrado.CONSULTAR_ASESOR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = EntregablesApiMessages.EntregableProyectoGrado.CONSULTAR_ASESOR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = EntregablesApiMessages.EntregableProyectoGrado.CONSULTAR_ASESOR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = EntregablesApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = EntregablesApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<PageResponseDTO<EntregableProyectoGradoResponseDTO>> consultarEntregablesAsesor(
            @RequestBody(required = false) QueryCriteriaRequestDTO request,
            IdentidadActual identidad) {

        var resultado = consultarEntregablesProyectoGradoAsesorInteractor.ejecutar(
                ConsultarEntregablesProyectoGradoAsesorRequestMapper.toQuery(request, identidad));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(EntregableProyectoGradoResponseMapper::toResponse)));
    }
}
