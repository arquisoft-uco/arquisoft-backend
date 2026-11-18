package com.arquisoft.seguridad.application.autorizacion.command.secondaryport;

public interface PoliticaRecursoOutputPort {

    boolean exigeSoloAutenticacion(String metodo, String recurso);
}
