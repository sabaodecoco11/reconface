package br.ufg.reconface.util;

import lombok.Data;

@Data
public class Response {
    private long id;
    private String codigo;
    private String mensagem;

    public Response(long id){
        this.id = id;
    }

    public Response(long id, String mensagem){
        this.id = id;
        this.mensagem = mensagem;
    }

    public Response(long id, String codigo, String mensagem){
        this.id = id;
        this.codigo = codigo;
        this.mensagem = mensagem;
    }
}
