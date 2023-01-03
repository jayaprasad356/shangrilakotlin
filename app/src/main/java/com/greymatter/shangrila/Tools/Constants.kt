package com.greymatter.shangrila.Tools

class Constants {

    companion object Cons {
        const val MainBaseUrl = "https://shangrila.greymatterworks.in/"

        //public static final String MainBaseUrl = "http://192.168.43.38/shangrila/";
        const val BaseUrl = MainBaseUrl + "api/"
        const val LOGIN_URL = BaseUrl + "login.php"
        const val SIGNUP_URL = BaseUrl + "signup.php"
        const val ADD_RECHARGE_URL = BaseUrl + "add-recharge.php"
        const val PAYBILL_URL = BaseUrl + "paybill.php"
        const val ADD_USER_URL = BaseUrl + "add_user.php"
        const val USER_LIST_URL = BaseUrl + "user_list.php"
        const val CALCULATE_BILL_URL = BaseUrl + "calculate-bill.php"
        const val TRANSACTIONSLIST_URL = BaseUrl + "transactions_list.php"
        const val ADD_TRANSACTION_URL = BaseUrl + "add_transaction.php"
        const val UPDATE_USER_URL = BaseUrl + "update_user.php"
        const val UPDATE_NEED_URL = BaseUrl + "update_need.php"
        const val DELETE_USER = BaseUrl + "delete_user.php"
        const val SUCCESS = "success"
        const val TOTAL_AMOUNT = "total_amount"
        const val MESSAGE = "message"
        const val GRAND_TOTAL = "grand_total"
        const val PROPERTY_TYPE = "property_type"
        const val BEDROOMS_COUNT = "bedrooms_count"
        const val EVC_CODE = "evc_code"
        const val PROFIT = "profit"
        const val YESTERDAY_BALANCE = "yesterday_balance"

        const val PIN = "pin"
        const val ID = "id"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val WALLET = "wallet"
        const val MANAGER_ID = "manager_id"
        const val USER_ID = "user_id"
        const val EMR_DAY = "emr_day"
        const val EMR_NIGHT = "emr_night"
        const val GMR = "gmr"
        const val AMOUNT = "amount"
        const val REMARKS = "remarks"
        const val NAME = "name"
        const val MOBILE = "mobile"
        const val DATA = "data"

        const val DATE = "date"
        const val TOTAL = "total"

    }
}