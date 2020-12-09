package br.ufg.reconface.util;

public class CoreMessage {
    //Facial Detection
    public static final String MS_CADASTRO_01 = "Cadastro da face realizado com sucesso!";
    public static final String ME_CADASTRO_01 = "Falha ao cadastrar imagem! O rosto não foi reconhecido.";
    public static final String MS_CADASTRO_02 = "Usuário possui face cadastrada!";
    public static final String ME_CADASTRO_02 = "Usuário não possui face cadastrada!";

    //Facial Recognition
    public static final String MS_RECON_01 = "Usuário reconhecido com sucesso!";
    public static final String ME_RECON_01 = "Usuário não coincide!";
    public static final String ME_RECON_02 = "Falha interna no reconhecimento!";

    //Other
    public static final String ME_UPLOAD_01 = "Imagem nula!";
}
