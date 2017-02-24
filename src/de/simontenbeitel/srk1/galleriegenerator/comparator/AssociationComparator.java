package de.simontenbeitel.srk1.galleriegenerator.comparator;

import de.simontenbeitel.srk1.galleriegenerator.model.Referee;

import java.util.Comparator;

public class AssociationComparator implements Comparator<Referee> {

    @Override
    public int compare(Referee referee1, Referee referee2) {
        int comparedQmax = Integer.compare(referee1.getQmax().number, referee2.getQmax().number);
        if (0 != comparedQmax)
            return comparedQmax;
        return new NameComparator().compare(referee1, referee2);
    }

}
