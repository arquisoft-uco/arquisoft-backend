package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web;

import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.annotation.FichasApiKeys;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.usecase.ConsultarFichasPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.fichas.infrastructure.web.FichasRoutes;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.dto.PageResponseDTO;
import com.arquisoft.shared.web.dto.query.QueryCriteriaRequestDTO;
import com.arquisoft.shared.web.openapi.ApiCodes;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiKeys.FichaPerfil.TAG_NAME, description = FichasApiKeys.FichaPerfil.TAG_DESCRIPTION)
public class ConsultarFichasPerfilController {

    private final ConsultarFichasPerfilUseCase consultarFichasPerfilUseCase;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    @PostMapping("${rutas.fichas.fichas-perfil.coordinador:/coordinador}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_FICHA_PERFIL_VIEW)
    @Operation(
            summary = FichasApiKeys.FichaPerfil.CONSULTAR_SUMMARY,
            description = FichasApiKeys.FichaPerfil.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = FichasRoutes.SECURITY_SCHEME)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = FichasApiKeys.FichaPerfil.CONSULTAR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiKeys.FichaPerfil.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiKeys.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiKeys.FichaPerfil.CONSULTAR_RESP_403)
    })
    public ResponseEntity<PageResponseDTO<FichaPerfilReadModel>> consultarFichasCoordinador(
            @RequestBody(required = false) QueryCriteriaRequestDTO request) {

        QueryCriteriaRequestDTO solicitud = request != null ? request : new QueryCriteriaRequestDTO();
        logger.debug(catalogo.obtener(FichaPerfilKey.LOG_CONSULTANDO),
                solicitud.getPagina(), solicitud.getTamanio());

        FichaPerfilCriteria criteria = FichaPerfilCriteria.builder()
                .pagina(solicitud.getPagina())
                .tamanio(solicitud.getTamanio())
                .ordenamiento(solicitud.parsearOrdenamiento())
                .raiz(solicitud.parsearFiltros())
                .build();

        PaginatedResult<FichaPerfilReadModel> resultado = consultarFichasPerfilUseCase.ejecutar(criteria);

        return ResponseEntity.ok(PageResponseDTO.from(resultado));
    }
}
