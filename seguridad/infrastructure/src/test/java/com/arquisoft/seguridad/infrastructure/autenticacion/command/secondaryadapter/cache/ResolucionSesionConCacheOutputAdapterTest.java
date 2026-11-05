package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.cache;

import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.ResolucionSesion;
import com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.pdp.ResolucionSesionPdpOutputPort;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionProperties;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResolucionSesionConCacheOutputAdapterTest {

    private static final String COOKIE = "SECURITY_BASELINE_SESSION=valor-secreto";
    private static final ResolucionSesion.Resuelta ANA = new ResolucionSesion.Resuelta("s1", "Ana", "a@uco.edu.co", "t");

    @Mock
    private ResolucionSesionPdpOutputPort pdp;

    @Mock
    private AppLogger logger;

    private ResolucionSesionConCacheOutputAdapter adaptador(Duration ttl) {
        var propiedades = new SesionProperties("sesion-pdp", true, "pep", "SECURITY_BASELINE_SESSION",
                "https://pdp.test", false, Duration.ofSeconds(1), Duration.ofSeconds(3),
                new SesionProperties.Cache(ttl, 100));
        return new ResolucionSesionConCacheOutputAdapter(pdp, propiedades, logger);
    }

    @Test
    void debeConsultarElPdpYGuardar_cuandoNoEstaEnCache() {
        // Arrange
        var adaptador = adaptador(Duration.ofSeconds(60));
        when(pdp.resolver(COOKIE)).thenReturn(ANA);

        // Act
        var primera = adaptador.resolver(COOKIE);
        var segunda = adaptador.resolver(COOKIE);

        // Assert
        assertThat(primera).isEqualTo(ANA);
        assertThat(segunda).isEqualTo(ANA);
        verify(pdp, times(1)).resolver(COOKIE);
    }

    @Test
    void debeNoGuardar_cuandoSinSesion() {
        // Arrange
        var adaptador = adaptador(Duration.ofSeconds(60));
        when(pdp.resolver(COOKIE)).thenReturn(new ResolucionSesion.SinSesion());

        // Act
        var primera = adaptador.resolver(COOKIE);
        var segunda = adaptador.resolver(COOKIE);

        // Assert
        assertThat(primera).isInstanceOf(ResolucionSesion.SinSesion.class);
        assertThat(segunda).isInstanceOf(ResolucionSesion.SinSesion.class);
        verify(pdp, times(2)).resolver(COOKIE);
    }

    @Test
    void debeDelegarSiempre_cuandoTtlCero() {
        // Arrange
        var adaptador = adaptador(Duration.ZERO);
        when(pdp.resolver(COOKIE)).thenReturn(ANA);

        // Act
        adaptador.resolver(COOKIE);
        adaptador.resolver(COOKIE);

        // Assert
        verify(pdp, times(2)).resolver(COOKIE);
    }

    @Test
    void debeUsarLaHuellaSha256YNoLaCookie_cuandoGuardaYLoguea() {
        // Arrange
        var adaptador = adaptador(Duration.ofSeconds(60));
        when(pdp.resolver(COOKIE)).thenReturn(ANA);

        // Act
        adaptador.resolver(COOKIE);
        var huella = ResolucionSesionConCacheOutputAdapter.huella("abc");

        // Assert
        assertThat(huella).isEqualTo("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
        assertThat(ResolucionSesionConCacheOutputAdapter.huella(COOKIE)).doesNotContain("valor-secreto");
        var argumentosLogueados = mockingDetails(logger).getInvocations().stream()
                .flatMap(invocacion -> java.util.Arrays.stream(invocacion.getArguments()))
                .map(String::valueOf)
                .toList();
        assertThat(argumentosLogueados).noneMatch(argumento -> argumento.contains("valor-secreto"));
    }
}
