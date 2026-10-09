package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.primaryport.interactor.ConsultarCategoriasItemCuantitativoJuradoInteractor;
import com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.query.primaryadapter.web.dto.CategoriaItemCuantitativoJuradoResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.query.primaryadapter.web.mapper.CategoriaItemCuantitativoJuradoResponseMapper;
import com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.query.primaryadapter.web.mapper.ConsultarCategoriasItemCuantitativoJuradoRequestMapper;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EvaluacionesApiMessages;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${rutas.evaluaciones.categorias-item-cuantitativo-jurado.base:/evaluaciones/categorias-item-cuantitativo-jurado}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.CategoriaItemCuantitativoJurado.TAG_NAME,
        description = EvaluacionesApiMessages.CategoriaItemCuantitativoJurado.TAG_DESCRIPTION)
public class ConsultarCategoriasItemCuantitativoJuradoController {

    private final ConsultarCategoriasItemCuantitativoJuradoInteractor interactor;

    @GetMapping
    @PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_CATEGORIA_ITEM_CUANTITATIVO_JURADO_VIEW)
    @Operation(
            summary = EvaluacionesApiMessages.CategoriaItemCuantitativoJurado.CONSULTAR_SUMMARY,
            description = EvaluacionesApiMessages.CategoriaItemCuantitativoJurado.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.CategoriaItemCuantitativoJurado.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoriaItemCuantitativoJuradoResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.CategoriaItemCuantitativoJurado.CONSULTAR_RESP_400),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<List<CategoriaItemCuantitativoJuradoResponseDTO>> consultarCategoriasItemCuantitativoJurado(
            @RequestParam(required = false) String nombre) {
        var query = ConsultarCategoriasItemCuantitativoJuradoRequestMapper.toQuery(nombre);
        var categorias = interactor.ejecutar(query);

        return ResponseEntity.ok(categorias.stream()
                .map(CategoriaItemCuantitativoJuradoResponseMapper::toResponse)
                .toList());
    }
}
