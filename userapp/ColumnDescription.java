package userapp;

public enum ColumnDescription {
    NOTHING("nothing"), ID("id"), NAME("name"), COORDINATE_X("coordinate x"), COORDINATE_Y("coordinate y"), OSCARS_COUNT("oscars count"), LENGTH("length"), GENRE("genre"), MPAA_RATING("mpaa rating"), OPERATOR_NAME("operator name"), OPERATOR_ID("operator id"), OPERATOR_EYE_COLOR("operator eye color"), OPERATOR_NATIONALITY("operator nationality"), CREATOR_NAME("creator name"), CREATION_TIME("creation time");
    private String description;
    private ColumnDescription(String description) {
        this.description = description;
    }
}
