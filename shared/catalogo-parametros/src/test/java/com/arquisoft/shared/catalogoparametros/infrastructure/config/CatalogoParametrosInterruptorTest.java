package com.arquisoft.shared.catalogoparametros.infrastructure.config;

import com.arquisoft.shared.catalogoparametros.application.command.finder.ParametroCatalogoFinder;
import com.arquisoft.shared.catalogoparametros.application.command.finder.impl.ParametroCatalogoFinderImpl;
import com.arquisoft.shared.catalogoparametros.application.command.primaryport.interactor.RefrescarParametrosCatalogoInteractor;
import com.arquisoft.shared.catalogoparametros.application.command.primaryport.interactor.impl.RefrescarParametrosCatalogoInteractorImpl;
import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.ParametrosCatalogoOutputPort;
import com.arquisoft.shared.catalogoparametros.application.command.usecase.impl.RefrescarParametrosCatalogoUseCaseImpl;
import com.arquisoft.shared.catalogoparametros.infrastructure.primaryadapter.arranque.CargaInicialParametrosCatalogo;
import com.arquisoft.shared.catalogoparametros.infrastructure.primaryadapter.scheduler.RefrescarParametrosCatalogoScheduler;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.memoria.ParametrosCatalogoMemoriaOutputAdapter;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.CatalogoParametrosHttpOutputAdapter;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.CredencialServicioCatalogoParametros;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.dto.ParametrosCatalogoResponseDTO;
import com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.webclient.dto.TokenServicioCatalogoResponseDTO;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.AlcanceTraza;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.webclient.ClienteHttp;
import com.arquisoft.shared.webclient.FabricaClienteHttp;
import com.arquisoft.shared.webclient.RespuestaHttp;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CatalogoParametrosInterruptorTest {

    private static final String TOKEN_ID = "svc-arquisoft-catalogo-dev";

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(
                    CatalogoParametrosConfig.class,
                    CatalogoParametrosClienteHttpConfig.class,
                    ValidadorConfiguracionCatalogoParametros.class,
                    ParametrosCatalogoMemoriaOutputAdapter.class,
                    ParametroCatalogoFinderImpl.class,
                    CredencialServicioCatalogoParametros.class,
                    CatalogoParametrosHttpOutputAdapter.class,
                    RefrescarParametrosCatalogoUseCaseImpl.class,
                    RefrescarParametrosCatalogoInteractorImpl.class,
                    CargaInicialParametrosCatalogo.class,
                    RefrescarParametrosCatalogoScheduler.class)
            .withBean("catalogoMensajesRedis", Object.class, Object::new)
            .withBean(AppLogger.class, () -> mock(AppLogger.class))
            .withBean(GestorTraza.class, () -> {
                var gestor = mock(GestorTraza.class);
                when(gestor.abrir(any())).thenReturn(mock(AlcanceTraza.class));
                return gestor;
            })
            .withBean(FabricaClienteHttp.class, () -> {
                var cliente = mock(ClienteHttp.class);
                when(cliente.ejecutar(any(), eq(ParametrosCatalogoResponseDTO.class)))
                        .thenReturn(new RespuestaHttp<>(200, new ParametrosCatalogoResponseDTO(List.of())));
                when(cliente.ejecutar(any(), eq(TokenServicioCatalogoResponseDTO.class)))
                        .thenReturn(new RespuestaHttp<>(200, new TokenServicioCatalogoResponseDTO("jwt", 300)));
                var fabrica = mock(FabricaClienteHttp.class);
                when(fabrica.crear(any())).thenReturn(cliente);
                return fabrica;
            });

    @Test
    void debeNoCrearBeansDeRefresco_cuandoApagado() {
        // Arrange / Act / Assert
        runner.withPropertyValues("catalogo.parametros.enabled=false").run(contexto -> {
            assertThat(contexto).hasNotFailed();
            assertThat(contexto).doesNotHaveBean(RefrescarParametrosCatalogoScheduler.class);
            assertThat(contexto).doesNotHaveBean(CargaInicialParametrosCatalogo.class);
            assertThat(contexto).doesNotHaveBean(ValidadorConfiguracionCatalogoParametros.class);
            assertThat(contexto).doesNotHaveBean(CatalogoParametrosHttpOutputAdapter.class);
            assertThat(contexto).doesNotHaveBean(CredencialServicioCatalogoParametros.class);
            assertThat(contexto).doesNotHaveBean(ClienteHttp.class);
            assertThat(contexto).doesNotHaveBean(RefrescarParametrosCatalogoUseCaseImpl.class);
            assertThat(contexto).doesNotHaveBean(RefrescarParametrosCatalogoInteractor.class);
            assertThat(contexto).hasSingleBean(ParametroCatalogoFinder.class);
            assertThat(contexto).hasSingleBean(ParametrosCatalogoOutputPort.class);
        });
    }

    @Test
    void debeCrearlosTodos_cuandoEncendido() {
        // Arrange / Act / Assert
        runner.withPropertyValues(
                "catalogo.parametros.enabled=true",
                "catalogo.parametros.base-url=https://catalogo.test/api/v1",
                "catalogo.parametros.token-uri=https://keycloak.test/realms/r/protocol/openid-connect/token",
                "catalogo.parametros.client-id=" + TOKEN_ID,
                "catalogo.parametros.client-secret=secreto-de-prueba-123").run(contexto -> {
            assertThat(contexto).hasNotFailed();
            assertThat(contexto).hasSingleBean(RefrescarParametrosCatalogoScheduler.class);
            assertThat(contexto).hasSingleBean(CargaInicialParametrosCatalogo.class);
            assertThat(contexto).hasSingleBean(ValidadorConfiguracionCatalogoParametros.class);
            assertThat(contexto).hasSingleBean(CatalogoParametrosHttpOutputAdapter.class);
            assertThat(contexto).hasSingleBean(CredencialServicioCatalogoParametros.class);
            assertThat(contexto).hasSingleBean(RefrescarParametrosCatalogoInteractor.class);
        });
    }

    @Test
    void debeAbortar_cuandoEncendidoSinConfiguracion() {
        // Arrange / Act / Assert
        runner.withPropertyValues("catalogo.parametros.enabled=true").run(contexto ->
                assertThat(contexto).hasFailed());
    }
}
