package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias;

import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.PoliticaRecursoOutputPort;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Component
public class PoliticaRecursoEquivalenciasOutputAdapter implements PoliticaRecursoOutputPort {

    private final Set<String> recursosSoloAutenticacion = new HashSet<>();

    public PoliticaRecursoEquivalenciasOutputAdapter(SoloAutenticacionProperties soloAutenticacion) {
        soloAutenticacion.entries().forEach(entrada -> recursosSoloAutenticacion.add(entrada.clave()));
    }

    @Override
    public boolean exigeSoloAutenticacion(String metodo, String recurso) {
        return recursosSoloAutenticacion.contains(metodo.toUpperCase(Locale.ROOT) + " " + recurso);
    }
}
