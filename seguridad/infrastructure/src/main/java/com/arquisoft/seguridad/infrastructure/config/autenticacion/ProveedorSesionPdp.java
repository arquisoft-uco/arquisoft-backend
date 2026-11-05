package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import com.arquisoft.seguridad.application.autenticacion.command.primaryport.interactor.ResolverIdentidadSesionInteractor;
import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.ResolverIdentidadSesionCommand;
import com.arquisoft.seguridad.application.autenticacion.command.result.ResolucionSesionResult;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.message.key.seguridad.ResolucionSesionKey;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProveedorSesionPdp implements ProveedorAutenticacion {

    private static final String SEPARADOR_COOKIE = "=";

    private final SesionProperties propiedades;
    private final ResolverIdentidadSesionInteractor resolverIdentidadSesionInteractor;

    @Override
    public String id() {
        return propiedades.id();
    }

    @Override
    public boolean habilitado() {
        return propiedades.enabled();
    }

    @Override
    public Optional<ModoAutorizacion> autorizacion(String emisor) {
        return propiedades.id().equals(emisor) ? Optional.of(ModoAutorizacion.PEP) : Optional.empty();
    }

    @Override
    public boolean exigeProteccionCsrf() {
        return true;
    }

    @Override
    public boolean autenticaPrimero() {
        return false;
    }

    @Override
    public boolean actuaEnRutasNoDeNegocio() {
        return false;
    }

    @Override
    public Optional<String> extraerCredencial(HttpServletRequest request) {
        var cookies = request.getCookies();
        if (UtilObjeto.esNulo(cookies)) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> propiedades.cookieName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(valor -> !UtilTexto.esVacioONulo(valor))
                .findFirst()
                .map(valor -> propiedades.cookieName() + SEPARADOR_COOKIE + valor);
    }

    @Override
    public void adjuntarCredencial(HttpHeaders cabeceras, String credencial) {
        cabeceras.set(HttpHeaders.COOKIE, credencial);
    }

    @Override
    public IdentidadAutenticada autenticar(String credencial) {
        var resultado = resolverIdentidadSesionInteractor.ejecutar(ResolverIdentidadSesionCommand.crear(credencial));
        return switch (resultado) {
            case ResolucionSesionResult.Resuelta resuelta -> new SesionAuthenticationToken(
                    propiedades.id(), resuelta.sujeto(), resuelta.nombre(), resuelta.correo(), resuelta.tenant());
            case ResolucionSesionResult.SinSesion ignorada ->
                    throw new InsufficientAuthenticationException(Mensajes.obtener(IniciarSesionKey.ERROR_HTTP_401));
            case ResolucionSesionResult.UsuarioNoRegistrado ignorada ->
                    throw new AccessDeniedException(Mensajes.obtener(ResolucionSesionKey.ERROR_USUARIO_NO_REGISTRADO));
        };
    }
}
