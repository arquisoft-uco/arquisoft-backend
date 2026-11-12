package com.arquisoft.entregables.infrastructure.artefacto.query.primaryadapter.web;

import com.arquisoft.entregables.application.artefacto.query.primaryport.interactor.ConsultarVistaPreviaPaqueteInteractor;
import com.arquisoft.entregables.infrastructure.artefacto.query.primaryadapter.web.dto.VistaPreviaPaqueteResponseDTO;
import com.arquisoft.entregables.infrastructure.artefacto.query.primaryadapter.web.mapper.ConsultarVistaPreviaPaqueteRequestMapper;
import com.arquisoft.entregables.infrastructure.artefacto.query.primaryadapter.web.mapper.VistaPreviaPaqueteResponseMapper;
import com.arquisoft.shared.message.annotation.ApiCodes;
import com.arquisoft.shared.message.annotation.ApiSecurity;
import com.arquisoft.shared.message.annotation.EntregablesApiMessages;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${rutas.entregables.entregables.base:/entregables}")
@RequiredArgsConstructor
@Tag(name = EntregablesApiMessages.VistaPreviaPaquete.TAG_NAME,
        description = EntregablesApiMessages.VistaPreviaPaquete.TAG_DESCRIPTION)
public class ConsultarVistaPreviaPaqueteController {

    private final ConsultarVistaPreviaPaqueteInteractor consultarVistaPreviaPaqueteInteractor;

    @GetMapping("${rutas.entregables.entregables.vista-previa-paquete:"
            + "/proyectos-grado/{proyectoGradoId}/vista-previa-paquete}")
    @Operation(
            summary = EntregablesApiMessages.VistaPreviaPaquete.CONSULTAR_SUMMARY,
            description = EntregablesApiMessages.VistaPreviaPaquete.CONSULTAR_DESCRIPTION,
            security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)
    )
    @ApiResponses({
            @ApiResponse(responseCode = ApiCodes.OK,
                    description = EntregablesApiMessages.VistaPreviaPaquete.CONSULTAR_RESP_200,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VistaPreviaPaqueteResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.BAD_REQUEST,
                    description = EntregablesApiMessages.VistaPreviaPaquete.CONSULTAR_RESP_400,
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = ApiCodes.UNAUTHORIZED,
                    description = EntregablesApiMessages.Comun.RESP_401),
            @ApiResponse(responseCode = ApiCodes.FORBIDDEN,
                    description = EntregablesApiMessages.Comun.RESP_403)
    })
    public ResponseEntity<VistaPreviaPaqueteResponseDTO> consultarVistaPreviaPaquete(
            @Parameter(description = EntregablesApiMessages.VistaPreviaPaquete.PARAM_PROYECTO_GRADO)
            @PathVariable UUID proyectoGradoId) {

        var artefactos = consultarVistaPreviaPaqueteInteractor.ejecutar(
                ConsultarVistaPreviaPaqueteRequestMapper.toQuery(proyectoGradoId));

        return ResponseEntity.ok(VistaPreviaPaqueteResponseMapper.toResponse(artefactos));
    }
}
