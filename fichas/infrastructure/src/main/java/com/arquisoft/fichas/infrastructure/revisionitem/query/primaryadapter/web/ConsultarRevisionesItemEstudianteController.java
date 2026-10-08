package com.arquisoft.fichas.infrastructure.revisionitem.query.primaryadapter.web;

import com.arquisoft.fichas.application.revisionitem.query.primaryport.interactor.ConsultarRevisionesItemEstudianteInteractor;
import com.arquisoft.fichas.infrastructure.revisionitem.query.primaryadapter.web.dto.RevisionItemResponseDTO;
import com.arquisoft.fichas.infrastructure.revisionitem.query.primaryadapter.web.mapper.ConsultarRevisionesItemEstudianteRequestMapper;
import com.arquisoft.fichas.infrastructure.revisionitem.query.primaryadapter.web.mapper.RevisionItemResponseMapper;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.shared.web.dto.PageResponseDTO;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.RevisionItem.TAG_NAME, description = FichasApiMessages.RevisionItem.TAG_DESCRIPTION)
public class ConsultarRevisionesItemEstudianteController {

    private final ConsultarRevisionesItemEstudianteInteractor consultarRevisionesItemEstudianteInteractor;

    @PostMapping("${rutas.fichas.fichas-perfil.revisiones-item-estudiante:/revisiones-item/estudiante}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_REVISION_ITEM_ESTUDIANTE_VIEW)
    @Operation(
            summary = FichasApiMessages.RevisionItem.CONSULTAR_ESTUDIANTE_SUMMARY,
            description = FichasApiMessages.RevisionItem.CONSULTAR_ESTUDIANTE_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = FichasApiMessages.RevisionItem.CONSULTAR_ESTUDIANTE_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.RevisionItem.CONSULTAR_ESTUDIANTE_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.RevisionItem.CONSULTAR_ESTUDIANTE_RESP_403)
    })
    public ResponseEntity<PageResponseDTO<RevisionItemResponseDTO>> consultarDeSuFicha(
            @RequestBody(required = false) QueryCriteriaRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        var estudiante = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());
        var resultado = consultarRevisionesItemEstudianteInteractor.ejecutar(
                ConsultarRevisionesItemEstudianteRequestMapper.toQuery(request, estudiante));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(RevisionItemResponseMapper::toResponse)));
    }
}
