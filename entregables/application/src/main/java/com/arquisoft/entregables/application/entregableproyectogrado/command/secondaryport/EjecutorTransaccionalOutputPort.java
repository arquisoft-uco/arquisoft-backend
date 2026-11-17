package com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport;

import java.util.function.Supplier;

public interface EjecutorTransaccionalOutputPort {

    <T> T enLectura(Supplier<T> accion);

    <T> T enEscritura(Supplier<T> accion);
}
