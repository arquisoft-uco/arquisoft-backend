package com.arquisoft.biblioteca.domain.bibliotecario.model;

import java.util.UUID;

public record VigenciaBibliotecario(UUID bibliotecario, boolean existe, boolean eliminado) {}
