package com.descarte.descarte.entitties;

public enum EnumPeriodo {
    DIURNO,
    NOTURNO;

    @Override
    public String toString() {
        switch (this) {
            case DIURNO: return "Diurno";
            case NOTURNO: return "Noturno";
            default: return super.toString();
        }
    }
}
