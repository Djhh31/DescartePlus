package com.descarte.descarte.entitties;

public class Data {

    public String Endereco;
    public String Subprefeitura;
    public String Cep;
    public Coleta Domiciliar;
    public Coleta Seletiva;

    public void setEndereco(String Tipo, String Titulo, String Preposicao, String Logradouro){

        StringBuilder sb = new StringBuilder();

        if (Tipo != null && !Tipo.equals("-")){
            sb.append(firstToUpper(Tipo)).append(". ");
        }

        if (Titulo != null && !Titulo.equals("-")){
            sb.append(firstToUpper(Titulo)).append(" ");
        }

        if (Preposicao != null && !Preposicao.equals("-")){
            sb.append(firstToUpper(Preposicao)).append(" ");
        }

        if (Logradouro != null && !Logradouro.equals("-")){
            sb.append(firstToUpper(Logradouro)).append(" ");
        }

        this.Endereco = sb.toString();
    }

    private String firstToUpper(String input){
        String[] words = input.split(" ");  // Dividir a string em palavras
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
}