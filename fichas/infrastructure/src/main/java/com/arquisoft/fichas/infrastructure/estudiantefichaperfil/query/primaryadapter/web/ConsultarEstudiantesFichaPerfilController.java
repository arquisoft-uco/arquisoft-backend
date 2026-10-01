package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.interactor.ConsultarEstudiantesFichaPerfilInteractor;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.primaryadapter.web.dto.EstudianteFichaPerfilResponseDTO;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.primaryadapter.web.mapper.ConsultarEstudiantesFichaPerfilRequestMapper;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.primaryadapter.web.mapper.EstudianteFichaPerfilResponseMapper;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.ConsultaEstudianteFichaPerfil.TAG_NAME,
        description = FichasApiMessages.ConsultaEstudianteFichaPerfil.TAG_DESCRIPTION)
public class ConsultarEstudiantesFichaPerfilController {

    private final ConsultarEstudiantesFichaPerfilInteractor consultarEstudiantesFichaPerfilInteractor;

    @GetMapping("${rutas.fichas.fichas-perfil.estudiantes:/{fichaPerfilId}/estudiantes}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ESTUDIANTE_FICHA_PERFIL_COORDINADOR_VIEW)
    @Operation(
            summary = FichasApiMessages.ConsultaEstudianteFichaPerfil.CONSULTAR_SUMMARY,
            description = FichasApiMessages.ConsultaEstudianteFichaPerfil.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = FichasApiMessages.ConsultaEstudianteFichaPerfil.CONSULTAR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EstudianteFichaPerfilResponseDTO.class)))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.ConsultaEstudianteFichaPerfil.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.ConsultaEstudianteFichaPerfil.CONSULTAR_RESP_403)
    })
    public ResponseEntity<List<EstudianteFichaPerfilResponseDTO>> consultarEstudiantes(
            @PathVariable UUID fichaPerfilId) {

        var estudiantes = consultarEstudiantesFichaPerfilInteractor.ejecutar(
                ConsultarEstudiantesFichaPerfilRequestMapper.toQuery(fichaPerfilId));

        return ResponseEntity.ok(estudiantes.stream()
                .map(EstudianteFichaPerfilResponseMapper::toResponse)
                .toList());
    }
}
