package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.primaryadapter.web;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.interactor.ConsultarEntregablesDisponiblesInteractor;
import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.primaryadapter.web.dto.ConsultarEntregablesDisponiblesRequestDTO;
import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.primaryadapter.web.dto.EntregableDisponibleResponseDTO;
import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.primaryadapter.web.mapper.ConsultarEntregablesDisponiblesRequestMapper;
import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.primaryadapter.web.mapper.EntregableDisponibleResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.BibliotecaApiMessages;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rutas.biblioteca.biblioteca.base:/biblioteca}"
        + "${rutas.biblioteca.biblioteca.entregables-proyecto-grado:/entregables-proyecto-grado}")
@RequiredArgsConstructor
@Tag(name = BibliotecaApiMessages.EntregableProyectoGrado.TAG_NAME,
        description = BibliotecaApiMessages.EntregableProyectoGrado.TAG_DESCRIPTION)
public class ConsultarEntregablesDisponiblesController {

    private final ConsultarEntregablesDisponiblesInteractor consultarEntregablesDisponiblesInteractor;

    @PostMapping("${rutas.biblioteca.biblioteca.consultar:/consultar}")
    @Operation(
            summary = BibliotecaApiMessages.EntregableProyectoGrado.CONSULTAR_DISPONIBLES_SUMMARY,
            description = BibliotecaApiMessages.EntregableProyectoGrado.CONSULTAR_DISPONIBLES_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = BibliotecaApiMessages.EntregableProyectoGrado.CONSULTAR_DISPONIBLES_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = BibliotecaApiMessages.EntregableProyectoGrado.CONSULTAR_DISPONIBLES_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = BibliotecaApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = BibliotecaApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<PageResponseDTO<EntregableDisponibleResponseDTO>> consultarEntregablesDisponibles(
            @RequestBody(required = false) ConsultarEntregablesDisponiblesRequestDTO request) {

        var resultado = consultarEntregablesDisponiblesInteractor.ejecutar(
                ConsultarEntregablesDisponiblesRequestMapper.toQuery(request));

        return ResponseEntity.ok(PageResponseDTO.from(
                resultado.map(EntregableDisponibleResponseMapper::toResponse)));
    }
}
