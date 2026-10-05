package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.primaryport.interactor.ConsultarCategoriasItemCuantitativoAsesorInteractor;
import com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.query.primaryadapter.web.dto.CategoriaItemCuantitativoAsesorResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.query.primaryadapter.web.mapper.CategoriaItemCuantitativoAsesorResponseMapper;
import com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.query.primaryadapter.web.mapper.ConsultarCategoriasItemCuantitativoAsesorRequestMapper;
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
@RequestMapping("${rutas.evaluaciones.categorias-item-cuantitativo-asesor.base:/evaluaciones/categorias-item-cuantitativo-asesor}")
@RequiredArgsConstructor
@Tag(
        name = EvaluacionesApiMessages.CategoriaItemCuantitativoAsesor.TAG_NAME,
        description = EvaluacionesApiMessages.CategoriaItemCuantitativoAsesor.TAG_DESCRIPTION)
public class ConsultarCategoriasItemCuantitativoAsesorController {

    private final ConsultarCategoriasItemCuantitativoAsesorInteractor interactor;

    @GetMapping
    @PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_CATEGORIA_ITEM_CUANTITATIVO_ASESOR_VIEW)
    @Operation(
            summary = EvaluacionesApiMessages.CategoriaItemCuantitativoAsesor.CONSULTAR_SUMMARY,
            description = EvaluacionesApiMessages.CategoriaItemCuantitativoAsesor.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))
    @ApiResponses({
            @ApiResponse(
                    responseCode = ApiCodes.OK,
                    description = EvaluacionesApiMessages.CategoriaItemCuantitativoAsesor.CONSULTAR_RESP_200,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoriaItemCuantitativoAsesorResponseDTO.class))),
            @ApiResponse(
                    responseCode = ApiCodes.BAD_REQUEST,
                    description = EvaluacionesApiMessages.CategoriaItemCuantitativoAsesor.CONSULTAR_RESP_400),
            @ApiResponse(
                    responseCode = ApiCodes.UNAUTHORIZED,
                    description = EvaluacionesApiMessages.Comun.RESP_401),
            @ApiResponse(
                    responseCode = ApiCodes.FORBIDDEN,
                    description = EvaluacionesApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<List<CategoriaItemCuantitativoAsesorResponseDTO>> consultarCategoriasItemCuantitativoAsesor(
            @RequestParam(required = false) String nombre) {
        var query = ConsultarCategoriasItemCuantitativoAsesorRequestMapper.toQuery(nombre);
        var categorias = interactor.ejecutar(query);

        return ResponseEntity.ok(categorias.stream()
                .map(CategoriaItemCuantitativoAsesorResponseMapper::toResponse)
                .toList());
    }
}
