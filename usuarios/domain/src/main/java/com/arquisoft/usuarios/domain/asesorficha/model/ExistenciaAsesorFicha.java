package com.arquisoft.usuarios.domain.asesorficha.model;

import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;

import java.util.UUID;

public record ExistenciaAsesorFicha(UUID usuario, AsesorFichaDomain asesorFicha) {}
