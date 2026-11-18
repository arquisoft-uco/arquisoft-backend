package com.arquisoft.biblioteca.domain.estudianteproyectogrado.model;

import java.util.List;
import java.util.UUID;

public record AutoresEntregable(UUID entregableProyectoGrado, List<AutorDeEntregable> autores) {}
