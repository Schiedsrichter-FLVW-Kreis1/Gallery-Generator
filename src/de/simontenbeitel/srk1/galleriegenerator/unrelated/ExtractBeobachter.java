package de.simontenbeitel.srk1.galleriegenerator.unrelated;

import de.simontenbeitel.srk1.galleriegenerator.Generator;
import de.simontenbeitel.srk1.galleriegenerator.model.Referee;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class ExtractBeobachter {

    public static void main(String[] args) throws Exception{
        InputStream oldgalleryInputStream = new FileInputStream(new File("old_gallery.html"));
        BufferedReader oldgalleryReader = new BufferedReader(new InputStreamReader(oldgalleryInputStream, "Cp1252"));
        List<String> lines = oldgalleryReader.lines().collect(Collectors.toList());
        while (!lines.get(0).equals("<h2 id=\"Beobachter\">Beobachter</h2>")) {
            lines.remove(0);
        }
        final List<Referee> referees = new Generator(new File("stammdaten.csv"), new File("picture_urls.csv"), new File("foerderkader.csv"), new File("beobachter.csv")).referees;
        StringBuilder sb = new StringBuilder();
        sb.append("Ausweisnr.;Name;Vorname;Bemerkung");
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line.startsWith("<h3 style=\"margin-top: 0;\">")) {
                String fullName = line.split("<h3 style=\"margin-top: 0;\">")[1].split("<")[0];
                String firstName = fullName.split(" ")[0];
                String lastName = fullName.split(" ")[1];
                String remark = "";
                String nextLine = lines.get(index + 1);
                if (nextLine.startsWith("<p style=\"margin: 0; font-size: 16px;\"><span style=\"font-size: 18px;\">")) {
                    remark = nextLine.split("<p style=\"margin: 0; font-size: 16px;\"><span style=\"font-size: 18px;\">")[1].split("<")[0];
                }
                boolean success = false;
                for(Referee referee : referees) {
                    if (referee.getFirstname().equalsIgnoreCase(firstName) && referee.getLastname().equalsIgnoreCase(lastName)) {
                        success = true;
                        sb.append("\n").append(referee.getIdNumber()).append(";").append(lastName).append(";").append(firstName).append(";").append(remark);
                    }
                }if (!success)
                    System.err.println("Did not find id no for " + fullName);
            }
        }

        System.out.print(sb.toString());
    }

}
