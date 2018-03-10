package br.edu.ufrgs.inf.bpm;

import br.edu.ufrgs.inf.bpm.wrapper.StardogWrapper;

import java.io.IOException;

public class StardogApp {
    public static void main(String[] args) throws IOException {
        StardogWrapper stardogWrapper = new StardogWrapper();
        String validation = stardogWrapper.getValidation(); // Impossível, mais de 1400 ICV (Versão free só aceita 20 ICV)
        System.out.println(validation);
    }
}
