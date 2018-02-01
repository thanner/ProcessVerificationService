package br.edu.ufrgs.inf.bpm;

import br.edu.ufrgs.inf.bpm.wrapper.StardogWrapper;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        StardogWrapper stardogWrapper = new StardogWrapper();
        String validation = stardogWrapper.getValidation();
        System.out.println(validation);
    }
}
