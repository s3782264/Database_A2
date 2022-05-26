public class constants{


    public static final int PERSON_NAME_SIZE = 70;
    public static final int BIRTH_DATE_SIZE = 8;
    public static final int BIRTH_PLACE_SIZE = 354;
    public static final int DEATH_DATE_SIZE = 8;
    public static final int FIELD_SIZE = 242;
    public static final int GENRE_SIZE = 386;
    public static final int INSTRUMENT_SIZE = 541;
    public static final int NATIONALITY_SIZE = 119;
    public static final int THUMBNAIL_SIZE = 292;
    public static final int WIKIPAGE_ID_SIZE = 4;
    public static final int DESCRIPTION_SIZE = 466;
    public static final int TOTAL_SIZE = PERSON_NAME_SIZE +
                                         BIRTH_DATE_SIZE +
                                         BIRTH_PLACE_SIZE +
                                         DEATH_DATE_SIZE +
                                         FIELD_SIZE +
                                         GENRE_SIZE +
                                         INSTRUMENT_SIZE +
                                         NATIONALITY_SIZE +
                                         THUMBNAIL_SIZE +
                                         WIKIPAGE_ID_SIZE +
                                         DESCRIPTION_SIZE;

   
    public static final int PERSON_NAME_OFFSET = 0;
    public static final int BIRTH_DATE_OFFSET = PERSON_NAME_SIZE;
}
