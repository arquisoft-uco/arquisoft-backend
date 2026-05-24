package com.arquisoft.fichas.infrastructure.adapter.in.web;

import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaperfil.command.port.in.RegistrarFichaPerfilInputPort;
import com.arquisoft.fichas.application.fichaperfil.query.port.in.ConsultarFichasPerfilInputPort;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.adapter.in.web.dto.RegistrarFichaPerfilRequestDTO;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.PaginationRequest;
import com.arquisoft.shared.web.dto.PageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fichas-perfil")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Fichas Perfil", description = "Gestión de fichas de perfil de proyectos de grado")
public class FichaPerfilInputAdapter {

    private final RegistrarFichaPerfilInputPort registrarFichaPerfilInputPort;
    private final ConsultarFichasPerfilInputPort consultarFichasPerfilInputPort;

    @PostMapping
    @PreAuthorize("hasAuthority('ficha:ficha:create')")
    @Operation(
            summary = "Registrar ficha de perfil",
            description = "Crea una nueva ficha de perfil de proyecto de grado con el título y asesor indicados.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ficha de perfil registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = com.arquisoft.shared.web.dto.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<Void> registrar(@Valid @RequestBody RegistrarFichaPerfilRequestDTO request) {
        log.debug("POST /fichas-perfil — id={}", request.getId());

        registrarFichaPerfilInputPort.ejecutar(new RegistrarFichaPerfilCommand(
                request.getId(),
                request.getTituloProyecto(),
                request.getAsesorFichaId()
        ));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/coordinador")
    @PreAuthorize("hasAuthority('ficha:ficha:view')")
    @Operation(
            summary = "Listar fichas de perfil paginadas",
            description = "Retorna el listado paginado de todas las fichas de perfil registradas. Acceso exclusivo para el rol coordinador.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos",
                    content = @Content(schema = @Schema(implementation = com.arquisoft.shared.web.dto.ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos — se requiere rol coordinador")
    })
    public ResponseEntity<PageResponseDTO<FichaPerfilReadModel>> consultarFichasCoordinador(
            PaginationRequest request) {
        log.debug("GET /fichas-perfil/coordinador — page={}, size={}", request.getPage(), request.getSize());

        PaginatedResult<FichaPerfilReadModel> resultado = consultarFichasPerfilInputPort.ejecutar(request);

        return ResponseEntity.ok(PageResponseDTO.from(resultado));
    }
}
