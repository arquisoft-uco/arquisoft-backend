package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor.ConsultarEvaluacionesFichaPerfilAsesorInteractor;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.dto.EvaluacionFichaPerfilAsesorResponseDTO;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.mapper.ConsultarEvaluacionesFichaPerfilAsesorRequestMapper;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.mapper.EvaluacionFichaPerfilAsesorResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.FichasApiMessages;
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
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.EvaluacionFichaPerfil.TAG_NAME,
        description = FichasApiMessages.EvaluacionFichaPerfil.TAG_DESCRIPTION)
public class ConsultarEvaluacionesFichaPerfilAsesorController {

    private final ConsultarEvaluacionesFichaPerfilAsesorInteractor consultarEvaluacionesFichaPerfilAsesorInteractor;

    @PostMapping("${rutas.fichas.fichas-perfil.evaluaciones-asesor:/evaluaciones/asesor}")
    @Operation(
            summary = FichasApiMessages.EvaluacionFichaPerfil.CONSULTAR_ASESOR_SUMMARY,
            description = FichasApiMessages.EvaluacionFichaPerfil.CONSULTAR_ASESOR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = FichasApiMessages.EvaluacionFichaPerfil.CONSULTAR_ASESOR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.EvaluacionFichaPerfil.CONSULTAR_ASESOR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.EvaluacionFichaPerfil.CONSULTAR_ASESOR_RESP_403)
    })
    public ResponseEntity<PageResponseDTO<EvaluacionFichaPerfilAsesorResponseDTO>> consultarEvaluacionesAsesor(
            @RequestBody(required = false) QueryCriteriaRequestDTO request,
            IdentidadActual identidad) {

        var asesorFicha = identidad.id();
        var resultado = consultarEvaluacionesFichaPerfilAsesorInteractor.ejecutar(
                ConsultarEvaluacionesFichaPerfilAsesorRequestMapper.toQuery(request, asesorFicha));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(EvaluacionFichaPerfilAsesorResponseMapper::toResponse)));
    }
}
