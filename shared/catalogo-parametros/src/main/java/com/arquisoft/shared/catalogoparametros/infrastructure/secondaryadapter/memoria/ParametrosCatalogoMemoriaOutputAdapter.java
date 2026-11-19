package com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.memoria;

import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.ParametrosCatalogoOutputPort;
import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.entity.ParametrosCatalogoEntity;
import com.arquisoft.shared.catalogoparametros.infrastructure.config.CatalogoParametrosProperties;
import com.arquisoft.shared.catalogoparametros.infrastructure.exception.ParametroCatalogoNoDisponibleException;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class ParametrosCatalogoMemoriaOutputAdapter implements ParametrosCatalogoOutputPort {

    private final CatalogoParametrosProperties propiedades;
    private final AtomicReference<Map<String, String>> copia = new AtomicReference<>(Map.of());
    private final AtomicBoolean suspendido = new AtomicBoolean(false);

    @Override
    public String valor(String nombre) {
        if (UtilTexto.esVacioONulo(nombre)) {
            throw new ParametroCatalogoNoDisponibleException(UtilTexto.VACIO);
        }
        var vigente = copia.get().get(nombre);
        if (UtilObjeto.noEsNulo(vigente)) {
            return vigente;
        }
        var porDefecto = propiedades.defaults().get(nombre);
        if (UtilObjeto.noEsNulo(porDefecto)) {
            return porDefecto;
        }
        throw new ParametroCatalogoNoDisponibleException(nombre);
    }

    @Override
    public void reemplazar(ParametrosCatalogoEntity parametros) {
        copia.set(parametros.valores());
    }

    @Override
    public boolean suspendido() {
        return suspendido.get();
    }

    @Override
    public void suspender() {
        suspendido.set(true);
    }
}
