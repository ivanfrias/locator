package locator.withus.pt.domain;

/**
 * Created by ivanfrias on 20/12/14.
 */
public enum GenderPositions {
    MALE("male", 0),
    FEMALE("female", 1),
    NOT_SPECIFIED("not_specified", 2);

    private long genderPosition;
    private String genderDescription;

    GenderPositions(String genderDescription, long position){
        this.genderPosition = position;
        this.genderDescription = genderDescription;
    }

    public long getPosition(){
        return this.genderPosition;
    }
    public String getGenderDescription(){
        return this.genderDescription;
    }

}
