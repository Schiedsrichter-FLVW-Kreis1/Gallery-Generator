package de.simontenbeitel.srk1.galleriegenerator.model;

public class Referee {

    private long idNumber;
    private String firstname;
    private String lastname;
    private String birthday;
    private String club;
    private League qmax;
    private String refereeSince;

    public long getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(long idNumber) {
        this.idNumber = idNumber;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public League getQmax() {
        return qmax;
    }

    public void setQmax(League qmax) {
        this.qmax = qmax;
    }

    public String getRefereeSince() {
        return refereeSince;
    }

    public void setRefereeSince(String refereeSince) {
        this.refereeSince = refereeSince;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Referee referee = (Referee) o;

        if (firstname != null ? !firstname.equals(referee.firstname) : referee.firstname != null) return false;
        return lastname != null ? lastname.equals(referee.lastname) : referee.lastname == null;

    }

    @Override
    public int hashCode() {
        return (int) (idNumber ^ (idNumber >>> 32));
    }

}
