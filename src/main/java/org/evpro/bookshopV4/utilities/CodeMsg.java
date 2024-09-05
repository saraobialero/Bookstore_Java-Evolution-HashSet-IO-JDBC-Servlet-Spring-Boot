package org.evpro.bookshopV4.utilities;

public class CodeMsg {
    public static final String NF_CODE = " Not found";

    public static final String NC_CODE = " There aren't any content ";
    public static final String ONF_CODE = "Operation not found ";
    public static final String UE_CODE = "Unexpected error ";
    public static final String MP_CODE = "Missing required parameter: ";
    public static final String AJ_FORMAT = "application/json ";

    //Db messages
    public static final String DB_CODE = "Database error";

    //Book messages
    public static final String NBF_CODE = "No books found ";
    public static final String EVB_CODE = "Error viewing book ";
    public static final String ABF_CODE = "Any book found ";
    public static final String EUB_CODE = "Error updating books ";

    //User messages
    public static final String EAU_CODE = "Error adding user ";
    public static final String AUF_CODE = "Any user found ";
    public static final String EVU_CODE = "Error viewing user ";
    public static final String NUP_CODE = "No user provided or deserialization failed ";

    //Borrow messages
    public static final String NB_CODE = "There aren't any books for the user";
    public static final String NBU_CODE = "There aren't any borrow for the user";
    public static final String EVBU_CODE = "Error viewing borrows";
}
