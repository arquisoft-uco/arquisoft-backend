package com.arquisoft.fichas.infrastructure.observacionitem.query.primaryadapter.web;

import com.arquisoft.fichas.application.observacionitem.query.primaryport.interactor.ConsultarObservacionesItemAsesorInteractor;
import com.arquisoft.fichas.infrastructure.observacionitem.query.primaryadapter.web.dto.ObservacionItemResponseDTO;
import com.arquisoft.fichas.infrastructure.observacionitem.query.primaryadapter.web.mapper.ConsultarObservacionesItemAsesorRequestMapper;
import com.arquisoft.fichas.infrastructure.observacionitem.query.primaryadapter.web.mapper.ObservacionItemResponseMapper;
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
@Tag(name = FichasApiMessages.ObservacionItem.TAG_NAME, description = FichasApiMessages.ObservacionItem.TAG_DESCRIPTION)
public class ConsultarObservacionesItemAsesorController {

    private final ConsultarObservacionesItemAsesorInteractor consultarObservacionesItemAsesorInteractor;

    @PostMapping("${rutas.fichas.fichas-perfil.observaciones-item-asesor:/observaciones-item/asesor}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_OBSERVACION_ITEM_ASESOR_VIEW)
    @Operation(
            summary = FichasApiMessages.ObservacionItem.CONSULTAR_ASESOR_SUMMARY,
            description = FichasApiMessages.ObservacionItem.CONSULTAR_ASESOR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = FichasApiMessages.ObservacionItem.CONSULTAR_ASESOR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = FichasApiMessages.ObservacionItem.CONSULTAR_ASESOR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.ObservacionItem.CONSULTAR_ASESOR_RESP_403)
    })
    public ResponseEntity<PageResponseDTO<ObservacionItemResponseDTO>> consultarElaboradas(
            @RequestBody(required = false) QueryCriteriaRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        var asesorFicha = UtilUUID.generarUUIDDesdeTexto(jwt.getSubject());
        var resultado = consultarObservacionesItemAsesorInteractor.ejecutar(
                ConsultarObservacionesItemAsesorRequestMapper.toQuery(request, asesorFicha));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(ObservacionItemResponseMapper::toResponse)));
    }
}
