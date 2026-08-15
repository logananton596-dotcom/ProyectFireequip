package com.fireequipmanager.backend.service.rules;

import com.fireequipmanager.backend.exception.BusinessException;

import java.time.LocalDate;

public final class EstadoInventarioRule {

    private EstadoInventarioRule() {
    }

    // Valida estado para cualquier inventario
    public static void validarEstado(
            Enum<?> estado,
            LocalDate fechaBaja,
            String motivoBaja,
            String estadoBaja) {

        if (estado == null) {
            throw new BusinessException("Debe registrar el estado");
        }

        if (!estado.name().equals(estadoBaja)) {
            return;
        }

        if (fechaBaja == null) {
            throw new BusinessException("Debe registrar la fecha de baja");
        }

        if (motivoBaja == null || motivoBaja.isBlank()) {
            throw new BusinessException("Debe registrar el motivo de baja");
        }
    }

}