package de.simontenbeitel.srk1.galleriegenerator;

import java.io.*;

public class Main {


    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        Generator generator = new Generator(new File("stammdaten.csv"), new File("picture_urls.csv"), new File("foerderkader.csv"), new File("beobachter.csv"));
        System.out.println(generator.getGalleryHtml());
    }

}
