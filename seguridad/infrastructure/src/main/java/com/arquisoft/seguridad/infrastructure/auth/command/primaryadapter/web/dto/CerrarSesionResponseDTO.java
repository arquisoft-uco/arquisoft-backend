package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.SesionKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CerrarSesionResponseDTO {

    @Builder.Default
    private String mensaje = Mensajes.obtener(SesionKey.CUERPO_SESION_CERRADA);
}
