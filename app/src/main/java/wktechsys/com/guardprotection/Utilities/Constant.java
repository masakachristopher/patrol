package wktechsys.com.guardprotection.Utilities;

public class Constant {

    public static final String PACKAGE_NAME = "wktechsys.com.guardprotection";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String RECEVIER = PACKAGE_NAME + ".RECEVIER";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    public static final String ADDRESS = PACKAGE_NAME + ".ADDRESS";
    public static final String LOCAITY = PACKAGE_NAME + ".LOCAITY";
    public static final String COUNTRY = PACKAGE_NAME + ".COUNTRY";
    public static final String DISTRICT = PACKAGE_NAME + ".DISTRICT";
    public static final String POST_CODE = PACKAGE_NAME + ".POST_CODE";
    public static final String STATE = PACKAGE_NAME + ".STATE";

    public static final int SUCCESS_RESULT = 1;
    public static final int FAILURE_RESULT = 0;

// server url
    public static final String BASE_API_URL = "https://qrpatrol.davidbilaza.com/api/";
    public static final String BASE_URL = "http://qrpatrol.davidbilaza.com/";

    // local urls
//    public static final String BASE_API_URL = "http://100.100.1.33/guardadmin/api/";
//    public static final String BASE_URL = "http://100.100.1.33/guardadmin/";


    public static final String LOGIN_URL = BASE_API_URL+"auth/login";
//    public static final String LOGIN_URL = "https://qrpatrol.davidbilaza.com/Api/login";
    public static final String GETDATA_URL = "http://qrpatrol.davidbilaza.com/Api/get_guards";
    public static final String SCAN_URL = BASE_API_URL+"scan_checkpoint";
    public static final String ABOUT_URL = BASE_API_URL+"about_us";
    public static final String ATTENDANCE_URL = BASE_API_URL+"get_attendance";
    public static final String MISSEDPT_URL = BASE_API_URL+"get_missed";
    public static final String LOGOUT_URL = BASE_API_URL+"sign_out";
    public static final String FORGETPASSWORD_URL = BASE_API_URL+"forget_password";
    public static final String ROUND_URL = BASE_API_URL+"get_rounds";
    public static final String HISTORY_URL = BASE_API_URL+"r_history";
    public static final String DETAILED_URL = BASE_API_URL+"r_history_detailed";
    public static final String HISTORYSORT_URL = BASE_API_URL+"history_by_date";
    public static final String INCIDENT_URL = BASE_API_URL+"get_incidents";
    public static final String ADD_REPORT_URL = BASE_API_URL+"add_report";
    public static final String START_SHIFT_URL = BASE_API_URL+"start_guard_shift";
    public static final String END_SHIFT_URL = BASE_API_URL+"end_guard_shift";





}
