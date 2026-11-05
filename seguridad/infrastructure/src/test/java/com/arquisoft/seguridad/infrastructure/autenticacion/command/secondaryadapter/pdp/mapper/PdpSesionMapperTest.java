package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.pdp.mapper;

import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.ResolucionSesion;
import com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.pdp.dto.SesionPdpResponseDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

class PdpSesionMapperTest {

    @Test
    void debeArmarCabeceras_cuandoHayCredencial() {
        // Act
        var cabeceras = PdpSesionMapper.toCabeceras("SECURITY_BASELINE_SESSION=abc");

        // Assert
        assertThat(cabeceras.getFirst(HttpHeaders.COOKIE)).isEqualTo("SECURITY_BASELINE_SESSION=abc");
        assertThat(cabeceras.getAccept()).containsExactly(MediaType.APPLICATION_JSON);
        assertThat(UtilUUID.generarUUIDDesdeTexto(cabeceras.getFirst(PdpSesionMapper.CABECERA_REQUEST_ID)))
                .isNotNull();
    }

    @Test
    void debeMapearRespuesta_cuandoTraeSujeto() {
        // Arrange
        var dto = new SesionPdpResponseDTO(new SesionPdpResponseDTO.Datos(" sub-1 ", " Ana ", " a@uco.edu.co ", " t-1 "));
        var sinSujeto = new SesionPdpResponseDTO(new SesionPdpResponseDTO.Datos("  ", "Ana", "a@uco.edu.co", "t-1"));

        // Act
        var resolucion = PdpSesionMapper.toResolucion(dto);

        // Assert
        assertThat(resolucion).isEqualTo(new ResolucionSesion.Resuelta("sub-1", "Ana", "a@uco.edu.co", "t-1"));
        assertThat(PdpSesionMapper.tieneSujeto(dto)).isTrue();
        assertThat(PdpSesionMapper.tieneSujeto(sinSujeto)).isFalse();
        assertThat(PdpSesionMapper.tieneSujeto(new SesionPdpResponseDTO(null))).isFalse();
        assertThat(PdpSesionMapper.tieneSujeto(null)).isFalse();
    }
}
