package de.simontenbeitel.srk1.galleriegenerator.mapper;

import de.simontenbeitel.srk1.galleriegenerator.League;
import de.simontenbeitel.srk1.galleriegenerator.Referee;

import java.util.function.Function;

public class MasterDataToRefereeMapper implements Function<String, Referee> {

    private static final String DELIMINATOR = ";";

    @Override
    public Referee apply(String line) {
        Referee referee = new Referee();
        String[] parts = line.split(DELIMINATOR);
        referee.setLastname(parts[0]);
        referee.setFirstname(parts[1]);
        referee.setBirthday(parts[9]);
        referee.setClub(parts[11]);
        referee.setIdNumber(Long.parseLong(parts[12]));
        referee.setRefereeSince(parts[13]);
        try {
            referee.setQmax(League.valueOf(parts[16]));
        } catch (RuntimeException ignored) {
        }
        return referee;
    }

}
