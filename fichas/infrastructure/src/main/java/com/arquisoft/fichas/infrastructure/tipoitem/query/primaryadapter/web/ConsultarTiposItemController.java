package com.arquisoft.fichas.infrastructure.tipoitem.query.primaryadapter.web;

import com.arquisoft.shared.message.annotation.FichasApiMessages;
import com.arquisoft.fichas.application.tipoitem.query.primaryport.interactor.ConsultarTiposItemInteractor;
import com.arquisoft.fichas.application.tipoitem.query.readmodel.TipoItemReadModel;
import com.arquisoft.fichas.infrastructure.tipoitem.query.primaryadapter.web.dto.TipoItemResponseDTO;
import com.arquisoft.fichas.infrastructure.tipoitem.query.primaryadapter.web.mapper.TipoItemResponseMapper;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.ApiCodes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
@Tag(name = FichasApiMessages.TipoItem.TAG_NAME, description = FichasApiMessages.TipoItem.TAG_DESCRIPTION)
public class ConsultarTiposItemController {

    private final ConsultarTiposItemInteractor consultarTiposItemInteractor;

    @GetMapping("${rutas.fichas.fichas-perfil.tipos-item:/tipos-item}")
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_TIPO_ITEM_VIEW)
    @Operation(
            summary = FichasApiMessages.TipoItem.CONSULTAR_SUMMARY,
            description = FichasApiMessages.TipoItem.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = FichasApiMessages.TipoItem.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TipoItemResponseDTO.class)
                    )),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = FichasApiMessages.TipoItem.CONSULTAR_RESP_401,
                    content = @Content),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = FichasApiMessages.TipoItem.CONSULTAR_RESP_403,
                    content = @Content)
    })
    public ResponseEntity<List<TipoItemResponseDTO>> consultarTiposItem() {
        List<TipoItemReadModel> tipos = consultarTiposItemInteractor.ejecutar();

        return ResponseEntity.ok(tipos.stream()
                .map(TipoItemResponseMapper::toResponse)
                .toList());
    }
}
