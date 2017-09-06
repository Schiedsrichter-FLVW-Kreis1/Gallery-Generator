package de.simontenbeitel.srk1.galleriegenerator;

import de.simontenbeitel.srk1.galleriegenerator.comparator.AssociationComparator;
import de.simontenbeitel.srk1.galleriegenerator.comparator.NameComparator;
import de.simontenbeitel.srk1.galleriegenerator.mapper.MasterDataToRefereeMapper;
import de.simontenbeitel.srk1.galleriegenerator.model.ImageWithThumbnail;
import de.simontenbeitel.srk1.galleriegenerator.model.Referee;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Generator {

    private static final String DELIMINATOR = ";";
    private static final String INTRODUCTION = "Hier stellen wir unsere <a href=\"#Beobachter\">Beobachter</a>, <a href=\"#WeitereLeistungskader\">Nachwuchshoffnungen</a> und <a href=\"#Verbandsschiedsrichter\">Spitzenschiedsrichter</a> vor.";
    private static final String HEADING_ASSOCIATION = "<h2 id=\"Verbandsschiedsrichter\">Unsere Verbandsschiedsrichter</h2>";
    private static final String HEADING_FK = "<h2 id=\"WeitereLeistungskader\">Weitere Schiedsrichter Leistungskader</h2>";
    private static final String HEADING_BEO = "<h2 id=\"Beobachter\">Beobachter</h2>";
    private static final String INTRO_GROUP = "<div class=\"ksa-group\">\n" +
            "<ul style=\"list-style: none;\">";
    private static final String END_GROUP = "</ul>\n</div>";

    private final File masterData;
    private final File imageUrlData;
    private final File fkData;
    private final File beoData;
    public List<Referee> referees;
    private Map<Long, ImageWithThumbnail> images = new HashMap<>();
    private Set<Long> fkMemberIdNumbers;
    private Map<Long, String> beo = new HashMap<>();

    public Generator(File masterData, File imageUrlData, File fkData, File beoData) throws FileNotFoundException {
        this.masterData = masterData;
        this.imageUrlData = imageUrlData;
        this.fkData = fkData;
        this.beoData = beoData;
        loadData();
    }

    private void loadData() throws FileNotFoundException {
        loadMasterData();
        loadImages();
        loadFkData();
        loadBeobachterData();
    }

    private void loadMasterData() throws FileNotFoundException {
        InputStream masterDataInputStream = new FileInputStream(masterData);
        BufferedReader masterDataReader = null;
        try {
            masterDataReader = new BufferedReader(new InputStreamReader(masterDataInputStream, "Cp1252"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        referees = masterDataReader.lines()
                .skip(3)
                .map(new MasterDataToRefereeMapper())
                .collect(Collectors.toList());
    }

    private void loadImages() throws FileNotFoundException {
        InputStream masterDataInputStream = new FileInputStream(imageUrlData);
        BufferedReader masterDataReader = null;
        try {
            masterDataReader = new BufferedReader(new InputStreamReader(masterDataInputStream, "Cp1252"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        masterDataReader.lines()
                .skip(1)
                .forEach(s -> {
                    String[] parts = s.split(DELIMINATOR);
                    try {
                        ImageWithThumbnail previous = images.put(Long.valueOf(parts[0]), new ImageWithThumbnail(parts[1], parts[2]));
                        if (null != previous)
                            System.err.println("Duplicate entry for " + parts[0]);
                    } catch (NumberFormatException ignored) {
                    }
                });
    }

    private void loadFkData() throws FileNotFoundException {
        InputStream fkInputStream = new FileInputStream(fkData);
        BufferedReader fkReader = null;
        try {
            fkReader = new BufferedReader(new InputStreamReader(fkInputStream, "Cp1252"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        fkMemberIdNumbers = fkReader.lines()
                .skip(1)
                .map(value -> value.split(DELIMINATOR, 2)[0].trim())
                .filter(value -> !value.isEmpty())
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    }

    private void loadBeobachterData() throws FileNotFoundException {
        InputStream beoInputStream = new FileInputStream(beoData);
        BufferedReader beoReader = null;
        try {
            beoReader = new BufferedReader(new InputStreamReader(beoInputStream, "Cp1252"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        beoReader.lines()
                .skip(1)
                .forEach(s -> {
                    String[] parts = s.split(DELIMINATOR);
                    if (parts[0].isEmpty()) return;
                    try {
                        beo.put(Long.valueOf(parts[0]), parts.length == 4 ? parts[3] : "");
                    } catch (NumberFormatException ignored) {
                    }
                });
    }

    public String getGalleryHtml() {
        final List<Referee> associationReferees = referees.stream()
                .filter(referee -> referee.getQmax() != null && referee.getQmax().isAssociation)
                .sorted(new AssociationComparator())
                .collect(Collectors.toList());
        final StringBuilder sb = new StringBuilder();
        sb.append(INTRODUCTION).append("\n");
        sb.append(HEADING_ASSOCIATION).append("\n");
        sb.append(INTRO_GROUP).append("\n");
        sb.append("<li style=\"padding: 5px 0 10px 0;\">").append("\n");
        sb.append("<div class=\"ksa_member\">").append("\n");
        Referee firstReferee = associationReferees.get(0);
        sb.append(getImageHtml(firstReferee)).append("\n");
        sb.append("<div class=\"ksa_member_infos\" style=\"float: left; margin-left: 20px;\">").append("\n");
        sb.append(getNameHtml(firstReferee)).append("\n");
        sb.append(getLeagueHtml(firstReferee)).append("\n");
        sb.append("Verein: ").append(firstReferee.getClub()).append("\n\n");
        sb.append("</div>").append("\n").append("<div class=\"clear\" style=\"clear: both;\"></div>").append("\n").append("</div></li>").append("\n");
        associationReferees.stream()
                .skip(1)
                .forEach(referee -> {
                    sb.append("<li style=\"padding: 10px 0; border-top: 3px dotted grey;\">").append("\n");
                    sb.append("<div class=\"ksa_member\">").append("\n");
                    sb.append(getImageHtml(referee)).append("\n");
                    sb.append("<div class=\"ksa_member_infos\" style=\"float: left; margin-left: 20px;\">").append("\n");
                    sb.append(getNameHtml(referee)).append("\n");
                    sb.append(getLeagueHtml(referee)).append("\n");
                    sb.append("Verein: ").append(referee.getClub()).append("\n\n");
                    sb.append("</div>").append("\n").append("<div class=\"clear\" style=\"clear: both;\"></div>").append("\n").append("</div></li>").append("\n");
                });
        sb.append(END_GROUP);
        sb.append("\n\n");

        sb.append(HEADING_FK).append("\n");
        final List<Referee> fkReferees = referees.stream()
                .filter(referee -> fkMemberIdNumbers.contains(referee.getIdNumber()))
                .filter(referee -> referee.getQmax() != null && !referee.getQmax().isAssociation)
                .sorted(new NameComparator())
                .collect(Collectors.toList());
        sb.append(INTRO_GROUP).append("\n");
        sb.append("<li style=\"padding: 5px 0 10px 0;\">").append("\n");
        sb.append("<div class=\"ksa_member\">").append("\n");
        firstReferee = fkReferees.get(0);
        sb.append(getImageHtml(firstReferee)).append("\n");
        sb.append("<div class=\"ksa_member_infos\" style=\"float: left; margin-left: 20px;\">").append("\n");
        sb.append(getNameHtml(firstReferee)).append("\n");
        sb.append("Geburtstag: ").append(firstReferee.getBirthday()).append("\n");
        sb.append("Verein: ").append(firstReferee.getClub()).append("\n");
        sb.append("SR seit: ").append(firstReferee.getRefereeSince()).append("\n");
        sb.append("Höchste Klasse: ").append(firstReferee.getQmax().description).append("\n\n");
        sb.append("</div>").append("\n").append("<div class=\"clear\" style=\"clear: both;\"></div>").append("\n").append("</div></li>").append("\n");
        fkReferees.stream()
                .skip(1)
                .forEach(referee -> {
                    sb.append("<li style=\"padding: 10px 0; border-top: 3px dotted grey;\">").append("\n");
                    sb.append("<div class=\"ksa_member\">").append("\n");
                    sb.append(getImageHtml(referee)).append("\n");
                    sb.append("<div class=\"ksa_member_infos\" style=\"float: left; margin-left: 20px;\">").append("\n");
                    sb.append(getNameHtml(referee)).append("\n");
                    sb.append("Geburtstag: ").append(referee.getBirthday()).append("\n");
                    sb.append("Verein: ").append(referee.getClub()).append("\n");
                    sb.append("SR seit: ").append(referee.getRefereeSince()).append("\n");
                    sb.append("Höchste Klasse: ").append(referee.getQmax().description).append("\n\n");
                    sb.append("</div>").append("\n").append("<div class=\"clear\" style=\"clear: both;\"></div>").append("\n").append("</div></li>").append("\n");
                });
        sb.append(END_GROUP);
        sb.append("\n\n");

        sb.append(HEADING_BEO).append("\n");
        final List<Referee> beoReferees = referees.stream()
                .filter(referee -> beo.containsKey(referee.getIdNumber()))
                .sorted(new NameComparator())
                .collect(Collectors.toList());
        sb.append(INTRO_GROUP).append("\n");
        sb.append("<li style=\"padding: 5px 0 10px 0;\">").append("\n");
        sb.append("<div class=\"ksa_member\">").append("\n");
        firstReferee = beoReferees.get(0);
        sb.append(getImageHtml(firstReferee)).append("\n");
        sb.append("<div class=\"ksa_member_infos\" style=\"float: left; margin-left: 20px;\">").append("\n");
        sb.append(getNameHtml(firstReferee)).append("\n");
        String remark = beo.get(firstReferee.getIdNumber());
        if (!remark.trim().isEmpty())
            sb.append("<p style=\"margin: 0; font-size: 16px;\"><span style=\"font-size: 18px;\">").append(remark).append("</span></p>").append("\n");
        sb.append("\n");
        sb.append("</div>").append("\n").append("<div class=\"clear\" style=\"clear: both;\"></div>").append("\n").append("</div></li>").append("\n");
        beoReferees.stream()
                .skip(1)
                .forEach(referee -> {
                    sb.append("<li style=\"padding: 10px 0; border-top: 3px dotted grey;\">").append("\n");
                    sb.append("<div class=\"ksa_member\">").append("\n");
                    sb.append(getImageHtml(referee)).append("\n");
                    sb.append("<div class=\"ksa_member_infos\" style=\"float: left; margin-left: 20px;\">").append("\n");
                    sb.append(getNameHtml(referee)).append("\n");
                    String innerremark = beo.get(referee.getIdNumber());
                    if (!innerremark.trim().isEmpty())
                        sb.append("<p style=\"margin: 0; font-size: 16px;\"><span style=\"font-size: 18px;\">").append(innerremark).append("</span></p>").append("\n");
                    sb.append("\n");
                    sb.append("</div>").append("\n").append("<div class=\"clear\" style=\"clear: both;\"></div>").append("\n").append("</div></li>").append("\n");
                });
        sb.append(END_GROUP);

        return sb.toString();
    }

    private String getImageHtml(Referee referee) {
        ImageWithThumbnail imageWithThumbnail = images.get(referee.getIdNumber());
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"ksa_member_image\" style=\"float: left; border: 1px solid black; width: 150px; height: 150px;\">");
        if (null == imageWithThumbnail) {
            sb.append("<img src=\"http://dummyimage.com/150/ffffff/000000.gif&amp;text=Bild+folgt\" alt=\"");
            sb.append(referee.getFirstname()).append(" ").append(referee.getLastname());
            sb.append("\" /></div>");
        } else {
            sb.append("<a href=\"").append(imageWithThumbnail.getImageUrl()).append("\">");
            sb.append("<img class=\"wp-image-186\" src=\"").append(imageWithThumbnail.getThumbnailUrl()).append("\" ");
            sb.append("alt=\"").append(referee.getFirstname()).append(" ").append(referee.getLastname()).append("\" />");
            sb.append("</a></div>");
        }
        return sb.toString();
    }

    private String getNameHtml(Referee referee) {
        return "<h3 style=\"margin-top: 0;\">" + referee.getFirstname() + " " + referee.getLastname() + "</h3>";
    }

    private String getLeagueHtml(Referee referee) {
        return "<p style=\"margin: 0; font-size: 16px;\"><span style=\"font-size: 18px;\">" + referee.getQmax().description + "</span></p>";
    }

}
