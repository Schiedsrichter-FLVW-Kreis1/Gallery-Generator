package de.simontenbeitel.srk1.galleriegenerator.unrelated;

import de.simontenbeitel.srk1.galleriegenerator.Generator;
import de.simontenbeitel.srk1.galleriegenerator.Referee;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ExtractImageUrlsFromHtml {

    private static final String DELIMINATOR = ";";

    private static class UrlObject {
        String url;
        String thumbnailUrl;
        String fullname;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UrlObject urlObject = (UrlObject) o;

            return fullname != null ? fullname.equals(urlObject.fullname) : urlObject.fullname == null;
        }

        @Override
        public int hashCode() {
            return fullname != null ? fullname.hashCode() : 0;
        }
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        InputStream oldgalleryInputStream = new FileInputStream(new File("old_gallery.html"));
        BufferedReader oldgalleryReader = new BufferedReader(new InputStreamReader(oldgalleryInputStream, "Cp1252"));
        List<String> lines = oldgalleryReader.lines().collect(Collectors.toList());
        System.out.println("");
        List<UrlObject> urlObjects = new LinkedList<>();
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (line.startsWith("<div class=\"ksa_member_image\"")) {
                if (line.contains("http://dummyimage.com/150/ffffff/000000.gif&amp;text=Bild+folgt")) continue;
                String[] parts = line.split("<a href=\"");
                String url = parts[1].split("\"", 2)[0];
                parts = parts[1].split(" src=\"", 2);
                String thumbnailUrl = parts[1].split("\"", 2)[0];
                for (int additionalIndex = index + 1; additionalIndex < lines.size(); additionalIndex++) {
                    line = lines.get(additionalIndex);
                    if (line.startsWith("<h3 style=\"margin-top: 0;\">")) {
                        parts = line.split("<h3 style=\"margin-top: 0;\">", 2);
                        String fullname = parts[1].split("<", 2)[0];
                        UrlObject object = new UrlObject();
                        object.url = url;
                        object.thumbnailUrl = thumbnailUrl;
                        object.fullname = fullname;
                        urlObjects.add(object);
                        break;
                    }
                }
            }
        }

        List<Referee> referees = new Generator(new File("stammdaten.csv"), new File("picture_urls.csv"), new File("foerderkader.csv"), new File("beobachter.csv")).referees;
        StringBuilder sb = new StringBuilder();
        sb.append("Ausweisnr." + DELIMINATOR + "URL" + DELIMINATOR + "ThumbnailURL\n");
        for (UrlObject object : urlObjects) {
            boolean success = false;
            String[] names = object.fullname.split(" ");
            for (Referee referee : referees) {
                if (names.length == 2 && referee.getFirstname().equalsIgnoreCase(names[0]) && referee.getLastname().equalsIgnoreCase(names[1])) {
                    success = true;
                    sb.append(referee.getIdNumber()).append(DELIMINATOR).append(object.url).append(DELIMINATOR).append(object.thumbnailUrl).append("\n");
                    break;
                } else if (names.length == 3 &&
                        (referee.getFirstname().equalsIgnoreCase(names[0] + " " + names[1]) && referee.getLastname().equalsIgnoreCase(names[2])
                                || referee.getFirstname().equalsIgnoreCase(names[0]) && referee.getLastname().equalsIgnoreCase(names[1] + " " + names[2]))) {
                    success = true;
                    sb.append(referee.getIdNumber()).append(DELIMINATOR).append(object.url).append(DELIMINATOR).append(object.thumbnailUrl).append("\n");
                    break;
                }
            }
            if (!success)
                System.err.println("Did not find id no for " + object.fullname + "\n" + object.url + "\n" + object.thumbnailUrl);
        }
        System.out.print(sb.toString());

    }

}
