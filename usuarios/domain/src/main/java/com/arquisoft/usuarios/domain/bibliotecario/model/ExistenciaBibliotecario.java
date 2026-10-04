package com.arquisoft.usuarios.domain.bibliotecario.model;

import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;

import java.util.UUID;

public record ExistenciaBibliotecario(UUID usuario, BibliotecarioDomain bibliotecario) {}
