package com.descarte.descarte.entitties;

import androidx.annotation.NonNull;

public class Coleta{
    public boolean isNull;
    public boolean Seg;
    public boolean Ter;
    public boolean Qua;
    public boolean Qui;
    public boolean Sex;
    public boolean Sab;
    public boolean Dom;
    public EnumPeriodo Periodo;

    public Coleta(boolean seg, boolean ter, boolean qua, boolean qui, boolean sex, boolean sab, boolean dom) {
        Seg = seg;
        Ter = ter;
        Qua = qua;
        Qui = qui;
        Sex = sex;
        Sab = sab;
        Dom = dom;
    }

    public Coleta(boolean isNull){
        isNull = isNull;
    }

    public void Periodo(String p){
        try {
            this.Periodo = Periodo.valueOf(p.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Valor inválido para Periodo: " + p);
        }
    }

    @NonNull
    @Override
    public String toString() {

        if (isNull){
            return "Não Atendido";
        }

        StringBuilder sb = new StringBuilder();
        boolean has = false;
        if (Seg){
            sb.append("Segunda");
            has = true;
        }
        if (Ter){
            if (has){
                sb.append(", ");
            }
            sb.append("Terça");
            has = true;
        }
        if (Qua) {
            if (has){
                sb.append(", ");
            }
            sb.append("Quarta");
            has = true;
        }
        if (Qui) {
            if (has){
                sb.append(", ");
            }
            sb.append("Quinta");
            has = true;
        }
        if (Sex) {
            if (has){
                sb.append(", ");
            }
            sb.append("Sexta");
            has = true;
        }
        if (Sab) {
            if (has){
                sb.append(", ");
            }
            sb.append("Sábado");
            has = true;
        }
        if (Dom) {
            if (has){
                sb.append(", ");
            }
            sb.append("Domingo");
            has = true;
        }
        if (!has){
            sb.append("Não Atendido");
        }

        return sb.toString();
    }
}
