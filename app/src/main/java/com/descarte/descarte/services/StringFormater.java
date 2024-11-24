package com.descarte.descarte.services;

import java.util.HashMap;
import java.util.Map;

public class StringFormater {

    private static final Map<String, String> abreviacoes = new HashMap<>();

    static {
        // Preenchendo o mapa de abreviações
        abreviacoes.put("r ", "rua ");
        abreviacoes.put("av ", "avenida ");
        abreviacoes.put("tv ", "travessa ");
        abreviacoes.put("pc ", "praça ");
        abreviacoes.put("al ", "alameda ");
        abreviacoes.put("lgo ", "largo ");
        abreviacoes.put("estr ", "estrada ");
        abreviacoes.put("rod ", "rodovia ");
        abreviacoes.put("bl ", "bloco ");
        abreviacoes.put("qd ", "quadra ");
        abreviacoes.put("cj ", "conjunto ");
        abreviacoes.put("bs ", "beco ");
        abreviacoes.put("v ", "viação ");
        abreviacoes.put("c ", "caminho ");

        // Abrev de titulos de logradouro
        abreviacoes.put("dr ", "doutor ");
        abreviacoes.put("prof ", "professor ");
        abreviacoes.put("com ", "comendador ");
        abreviacoes.put("eng ", "engenheiro ");
        abreviacoes.put("cel ", "coronel ");
        abreviacoes.put("v ", "visconde ");
        abreviacoes.put("sra ", "senhora ");
        abreviacoes.put("m ", "marechal ");
        abreviacoes.put("tte ", "tenente ");
    }

    public static String firstToUpper(String text){
        String[] words = text.split(" ");  // Dividir a string em palavras
        StringBuilder sb = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {  // Verificar se a palavra não está vazia
                sb.append(word.substring(0, 1).toUpperCase())  // Primeira letra em maiúscula
                        .append(word.substring(1).toLowerCase())  // Resto da palavra em minúscula
                        .append(" ");  // Adicionar espaço entre as palavras
            }
        }

        return sb.toString().trim();
    }

    // Transformar Tipo de rua abreviado no extenso
    public static String extend(String text){
        text = configure(text);
        for (Map.Entry<String, String> entry : abreviacoes.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return text.trim();
    }

    // Transformar Tipo de rua no modo abreviado
    public static String abbrev(String text){
        text = configure(text);
        for (Map.Entry<String, String> entry : abreviacoes.entrySet()) {
            text = text.replace(entry.getValue(), entry.getKey());
        }
        return text.trim();
    }

    private static String configure(String text){
        text = text.toLowerCase();
        text = text + " ";
        text = text.replaceAll("[^a-zA-Z0-9\\s]", "");
        text = text.replace(".", "");
        return text;
    }

    private static String noSpaces(String text){
        text = text.replaceAll(" ", "+");
        return text;
    }


}
