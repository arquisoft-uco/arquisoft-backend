package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.ConsultarFichasPerfilDisponiblesEvaluacionInteractor;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.dto.FichaPerfilResponseDTO;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.mapper.ConsultarFichasPerfilDisponiblesEvaluacionRequestMapper;
import com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.mapper.FichaPerfilResponseMapper;
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
@Tag(name = FichasApiMessages.FichaPerfil.TAG_NAME, description = FichasApiMessages.FichaPerfil.TAG_DESCRIPTION)
public class ConsultarFichasPerfilDisponiblesEvaluacionController {

    private final ConsultarFichasPerfilDisponiblesEvaluacionInteractor consultarFichasPerfilDisponiblesEvaluacionInteractor;

    @PostMapping("${rutas.fichas.fichas-perfil.disponibles-evaluacion:/disponibles-evaluacion}")
    @Operation(
            summary = FichasApiMessages.FichaPerfil.CONSULTAR_DISPONIBLES_EVALUACION_SUMMARY,
            description = FichasApiMessages.FichaPerfil.CONSULTAR_DISPONIBLES_EVALUACION_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = FichasApiMessages.FichaPerfil.CONSULTAR_DISPONIBLES_EVALUACION_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.FichaPerfil.CONSULTAR_DISPONIBLES_EVALUACION_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.FichaPerfil.CONSULTAR_DISPONIBLES_EVALUACION_RESP_403)
    })
    public ResponseEntity<PageResponseDTO<FichaPerfilResponseDTO>> consultarFichasDisponiblesEvaluacion(
            @RequestBody(required = false) QueryCriteriaRequestDTO request,
            IdentidadActual identidad) {

        var representanteComite = identidad.id();
        var resultado = consultarFichasPerfilDisponiblesEvaluacionInteractor.ejecutar(
                ConsultarFichasPerfilDisponiblesEvaluacionRequestMapper.toQuery(request, representanteComite));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(FichaPerfilResponseMapper::toResponse)));
    }
}
