package de.simontenbeitel.srk1.galleriegenerator.comparator;

import de.simontenbeitel.srk1.galleriegenerator.model.Referee;

import java.util.Comparator;

public class NameComparator implements Comparator<Referee> {

    @Override
    public int compare(Referee referee1, Referee referee2) {
        int comparedLastname = referee1.getLastname().compareTo(referee2.getLastname());
        if (0 != comparedLastname)
            return comparedLastname;
        return referee1.getFirstname().compareTo(referee2.getFirstname());
    }

}
