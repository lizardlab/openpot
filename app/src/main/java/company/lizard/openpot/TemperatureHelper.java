package company.lizard.openpot;

import android.util.Log;

import java.util.HashMap;

public class TemperatureHelper {
    private static final String TAG = TemperatureHelper.class.getSimpleName();
    private static HashMap<String, String> mADToCMap = new HashMap<>();
    private static HashMap<String, String> mCToADMap = new HashMap<>();

    static {
        mCToADMap.put("0", "8");
        mCToADMap.put("1", "9");
        mCToADMap.put("2", "9");
        mCToADMap.put("3", "9");
        mCToADMap.put("4", "10");
        mCToADMap.put("5", "10");
        mCToADMap.put("6", "11");
        mCToADMap.put("7", "11");
        mCToADMap.put("8", "12");
        mCToADMap.put("9", "12");
        mCToADMap.put("10", "13");
        mCToADMap.put("11", "13");
        mCToADMap.put("12", "14");
        mCToADMap.put("13", "15");
        mCToADMap.put("14", "15");
        mCToADMap.put("15", "16");
        mCToADMap.put("16", "17");
        mCToADMap.put("17", "17");
        mCToADMap.put("18", "18");
        mCToADMap.put("19", "19");
        mCToADMap.put("20", "19");
        mCToADMap.put("21", "20");
        mCToADMap.put("22", "21");
        mCToADMap.put("23", "22");
        mCToADMap.put("24", "23");
        mCToADMap.put("25", "24");
        mCToADMap.put("26", "25");
        mCToADMap.put("27", "25");
        mCToADMap.put("28", "26");
        mCToADMap.put("29", "27");
        mCToADMap.put("30", "28");
        mCToADMap.put("31", "29");
        mCToADMap.put("32", "30");
        mCToADMap.put("33", "32");
        mCToADMap.put("34", "33");
        mCToADMap.put("35", "34");
        mCToADMap.put("36", "35");
        mCToADMap.put("37", "36");
        mCToADMap.put("38", "37");
        mCToADMap.put("39", "39");
        mCToADMap.put("40", "40");
        mCToADMap.put("41", "41");
        mCToADMap.put("42", "43");
        mCToADMap.put("43", "44");
        mCToADMap.put("44", "45");
        mCToADMap.put("45", "47");
        mCToADMap.put("46", "48");
        mCToADMap.put("47", "50");
        mCToADMap.put("48", "51");
        mCToADMap.put("49", "53");
        mCToADMap.put("50", "54");
        mCToADMap.put("51", "55");
        mCToADMap.put("52", "57");
        mCToADMap.put("53", "58");
        mCToADMap.put("54", "60");
        mCToADMap.put("55", "62");
        mCToADMap.put("56", "64");
        mCToADMap.put("57", "65");
        mCToADMap.put("58", "67");
        mCToADMap.put("59", "68");
        mCToADMap.put("60", "71");
        mCToADMap.put("61", "72");
        mCToADMap.put("62", "74");
        mCToADMap.put("63", "75");
        mCToADMap.put("64", "77");
        mCToADMap.put("65", "79");
        mCToADMap.put("66", "81");
        mCToADMap.put("67", "83");
        mCToADMap.put("68", "85");
        mCToADMap.put("69", "87");
        mCToADMap.put("70", "89");
        mCToADMap.put("71", "90");
        mCToADMap.put("72", "93");
        mCToADMap.put("73", "94");
        mCToADMap.put("74", "96");
        mCToADMap.put("75", "98");
        mCToADMap.put("76", "100");
        mCToADMap.put("77", "102");
        mCToADMap.put("78", "104");
        mCToADMap.put("79", "106");
        mCToADMap.put("80", "108");
        mCToADMap.put("81", "110");
        mCToADMap.put("82", "112");
        mCToADMap.put("83", "114");
        mCToADMap.put("84", "116");
        mCToADMap.put("85", "118");
        mCToADMap.put("86", "119");
        mCToADMap.put("87", "121");
        mCToADMap.put("88", "123");
        mCToADMap.put("89", "125");
        mCToADMap.put("90", "127");
        mCToADMap.put("91", "129");
        mCToADMap.put("92", "131");
        mCToADMap.put("93", "133");
        mCToADMap.put("94", "134");
        mCToADMap.put("95", "136");
        mCToADMap.put("96", "138");
        mCToADMap.put("97", "140");
        mCToADMap.put("98", "142");
        mCToADMap.put("99", "144");
        mCToADMap.put("100", "145");
        mCToADMap.put("101", "147");
        mCToADMap.put("102", "149");
        mCToADMap.put("103", "150");
        mCToADMap.put("104", "152");
        mCToADMap.put("105", "154");
        mCToADMap.put("106", "156");
        mCToADMap.put("107", "158");
        mCToADMap.put("108", "160");
        mCToADMap.put("109", "161");
        mCToADMap.put("110", "162");
        mCToADMap.put("111", "164");
        mCToADMap.put("112", "166");
        mCToADMap.put("113", "167");
        mCToADMap.put("114", "169");
        mCToADMap.put("115", "170");
        mCToADMap.put("116", "172");
        mCToADMap.put("117", "173");
        mCToADMap.put("118", "175");
        mCToADMap.put("119", "176");
        mCToADMap.put("120", "177");
        mCToADMap.put("121", "179");
        mCToADMap.put("122", "180");
        mCToADMap.put("123", "181");
        mCToADMap.put("124", "182");
        mCToADMap.put("125", "184");
        mCToADMap.put("126", "185");
        mCToADMap.put("127", "187");
        mCToADMap.put("128", "188");
        mCToADMap.put("129", "189");
        mCToADMap.put("130", "190");
        mCToADMap.put("131", "192");
        mCToADMap.put("132", "193");
        mCToADMap.put("133", "194");
        mCToADMap.put("134", "195");
        mCToADMap.put("135", "196");
        mCToADMap.put("136", "197");
        mCToADMap.put("137", "198");
        mCToADMap.put("138", "199");
        mCToADMap.put("139", "200");
        mCToADMap.put("140", "201");
        mCToADMap.put("141", "202");
        mCToADMap.put("142", "203");
        mCToADMap.put("143", "204");
        mCToADMap.put("144", "205");
        mCToADMap.put("145", "206");
        mCToADMap.put("146", "207");
        mCToADMap.put("147", "208");
        mCToADMap.put("148", "209");
        mCToADMap.put("149", "210");
        mCToADMap.put("150", "210");
        mCToADMap.put("151", "211");
        mCToADMap.put("152", "212");
        mCToADMap.put("153", "213");
        mCToADMap.put("154", "214");
        mCToADMap.put("155", "214");
        mCToADMap.put("156", "215");
        mCToADMap.put("157", "216");
        mCToADMap.put("158", "217");
        mCToADMap.put("159", "217");
        mCToADMap.put("160", "218");
        mCToADMap.put("161", "219");
        mCToADMap.put("162", "219");
        mCToADMap.put("163", "220");
        mCToADMap.put("164", "221");
        mCToADMap.put("165", "221");
        mCToADMap.put("166", "222");
        mCToADMap.put("167", "223");
        mCToADMap.put("168", "223");
        mCToADMap.put("169", "224");
        mCToADMap.put("170", "224");
        mCToADMap.put("171", "225");
        mCToADMap.put("172", "225");
        mCToADMap.put("173", "226");
        mCToADMap.put("174", "226");
        mCToADMap.put("175", "227");
        mCToADMap.put("176", "227");
        mCToADMap.put("177", "228");
        mCToADMap.put("178", "228");
        mCToADMap.put("179", "229");
        mCToADMap.put("180", "229");
        mCToADMap.put("181", "230");
        mCToADMap.put("182", "230");
        mCToADMap.put("183", "231");
        mCToADMap.put("184", "231");
        mCToADMap.put("185", "231");
        mCToADMap.put("186", "232");
        mCToADMap.put("187", "232");
        mCToADMap.put("188", "233");
        mCToADMap.put("189", "233");
        mCToADMap.put("190", "233");
        mCToADMap.put("191", "234");
        mCToADMap.put("192", "234");
        mCToADMap.put("193", "235");
        mCToADMap.put("194", "235");
        mCToADMap.put("195", "235");
        mCToADMap.put("196", "236");
        mCToADMap.put("197", "236");
        mCToADMap.put("198", "236");
        mCToADMap.put("199", "237");
        mCToADMap.put("200", "237");
        mCToADMap.put("201", "238");
        mCToADMap.put("202", "238");
        mCToADMap.put("203", "238");
        mCToADMap.put("204", "238");
        mCToADMap.put("205", "239");
        mCToADMap.put("206", "239");
        mCToADMap.put("207", "239");
        mCToADMap.put("208", "239");
        mCToADMap.put("209", "240");
        mCToADMap.put("210", "240");
        mADToCMap.put("0", "-27");
        mADToCMap.put("1", "-27");
        mADToCMap.put("2", "-27");
        mADToCMap.put("3", "0");
        mADToCMap.put("4", "0");
        mADToCMap.put("5", "0");
        mADToCMap.put("6", "0");
        mADToCMap.put("7", "0");
        mADToCMap.put("8", "0");
        mADToCMap.put("9", "2");
        mADToCMap.put("10", "4");
        mADToCMap.put("11", "6");
        mADToCMap.put("12", "8");
        mADToCMap.put("13", "10");
        mADToCMap.put("14", "12");
        mADToCMap.put("15", "14");
        mADToCMap.put("16", "15");
        mADToCMap.put("17", "16");
        mADToCMap.put("18", "18");
        mADToCMap.put("19", "19");
        mADToCMap.put("20", "21");
        mADToCMap.put("21", "22");
        mADToCMap.put("22", "23");
        mADToCMap.put("23", "24");
        mADToCMap.put("24", "25");
        mADToCMap.put("25", "26");
        mADToCMap.put("26", "28");
        mADToCMap.put("27", "29");
        mADToCMap.put("28", "30");
        mADToCMap.put("29", "31");
        mADToCMap.put("30", "32");
        mADToCMap.put("31", "32");
        mADToCMap.put("32", "33");
        mADToCMap.put("33", "34");
        mADToCMap.put("34", "35");
        mADToCMap.put("35", "36");
        mADToCMap.put("36", "37");
        mADToCMap.put("37", "38");
        mADToCMap.put("38", "39");
        mADToCMap.put("39", "40");
        mADToCMap.put("40", "41");
        mADToCMap.put("41", "41");
        mADToCMap.put("42", "41");
        mADToCMap.put("43", "42");
        mADToCMap.put("44", "43");
        mADToCMap.put("45", "44");
        mADToCMap.put("46", "44");
        mADToCMap.put("47", "45");
        mADToCMap.put("48", "46");
        mADToCMap.put("49", "46");
        mADToCMap.put("50", "47");
        mADToCMap.put("51", "48");
        mADToCMap.put("52", "48");
        mADToCMap.put("53", "49");
        mADToCMap.put("54", "50");
        mADToCMap.put("55", "51");
        mADToCMap.put("56", "51");
        mADToCMap.put("57", "52");
        mADToCMap.put("58", "53");
        mADToCMap.put("59", "53");
        mADToCMap.put("60", "54");
        mADToCMap.put("61", "54");
        mADToCMap.put("62", "55");
        mADToCMap.put("63", "55");
        mADToCMap.put("64", "56");
        mADToCMap.put("65", "57");
        mADToCMap.put("66", "57");
        mADToCMap.put("67", "58");
        mADToCMap.put("68", "59");
        mADToCMap.put("69", "59");
        mADToCMap.put("70", "59");
        mADToCMap.put("71", "60");
        mADToCMap.put("72", "61");
        mADToCMap.put("73", "61");
        mADToCMap.put("74", "62");
        mADToCMap.put("75", "63");
        mADToCMap.put("76", "62");
        mADToCMap.put("77", "64");
        mADToCMap.put("78", "63");
        mADToCMap.put("79", "65");
        mADToCMap.put("80", "65");
        mADToCMap.put("81", "66");
        mADToCMap.put("82", "66");
        mADToCMap.put("83", "67");
        mADToCMap.put("84", "67");
        mADToCMap.put("85", "68");
        mADToCMap.put("86", "68");
        mADToCMap.put("87", "69");
        mADToCMap.put("88", "69");
        mADToCMap.put("89", "70");
        mADToCMap.put("90", "71");
        mADToCMap.put("91", "71");
        mADToCMap.put("92", "71");
        mADToCMap.put("93", "72");
        mADToCMap.put("94", "73");
        mADToCMap.put("95", "73");
        mADToCMap.put("96", "74");
        mADToCMap.put("97", "74");
        mADToCMap.put("98", "75");
        mADToCMap.put("99", "75");
        mADToCMap.put("100", "76");
        mADToCMap.put("101", "76");
        mADToCMap.put("102", "77");
        mADToCMap.put("103", "77");
        mADToCMap.put("104", "78");
        mADToCMap.put("105", "78");
        mADToCMap.put("106", "79");
        mADToCMap.put("107", "79");
        mADToCMap.put("108", "80");
        mADToCMap.put("109", "80");
        mADToCMap.put("110", "81");
        mADToCMap.put("111", "81");
        mADToCMap.put("112", "82");
        mADToCMap.put("113", "82");
        mADToCMap.put("114", "83");
        mADToCMap.put("115", "83");
        mADToCMap.put("116", "84");
        mADToCMap.put("117", "84");
        mADToCMap.put("118", "85");
        mADToCMap.put("119", "86");
        mADToCMap.put("120", "86");
        mADToCMap.put("121", "87");
        mADToCMap.put("122", "87");
        mADToCMap.put("123", "88");
        mADToCMap.put("124", "88");
        mADToCMap.put("125", "89");
        mADToCMap.put("126", "89");
        mADToCMap.put("127", "90");
        mADToCMap.put("128", "90");
        mADToCMap.put("129", "91");
        mADToCMap.put("130", "91");
        mADToCMap.put("131", "92");
        mADToCMap.put("132", "92");
        mADToCMap.put("133", "93");
        mADToCMap.put("134", "94");
        mADToCMap.put("135", "94");
        mADToCMap.put("136", "95");
        mADToCMap.put("137", "95");
        mADToCMap.put("138", "96");
        mADToCMap.put("139", "96");
        mADToCMap.put("140", "97");
        mADToCMap.put("141", "97");
        mADToCMap.put("142", "98");
        mADToCMap.put("143", "98");
        mADToCMap.put("144", "99");
        mADToCMap.put("145", "100");
        mADToCMap.put("146", "100");
        mADToCMap.put("147", "101");
        mADToCMap.put("148", "101");
        mADToCMap.put("149", "102");
        mADToCMap.put("150", "103");
        mADToCMap.put("151", "103");
        mADToCMap.put("152", "104");
        mADToCMap.put("153", "104");
        mADToCMap.put("154", "105");
        mADToCMap.put("155", "105");
        mADToCMap.put("156", "106");
        mADToCMap.put("157", "106");
        mADToCMap.put("158", "107");
        mADToCMap.put("159", "107");
        mADToCMap.put("160", "108");
        mADToCMap.put("161", "109");
        mADToCMap.put("162", "110");
        mADToCMap.put("163", "110");
        mADToCMap.put("164", "111");
        mADToCMap.put("165", "111");
        mADToCMap.put("166", "112");
        mADToCMap.put("167", "113");
        mADToCMap.put("168", "113");
        mADToCMap.put("169", "114");
        mADToCMap.put("170", "115");
        mADToCMap.put("171", "115");
        mADToCMap.put("172", "116");
        mADToCMap.put("173", "117");
        mADToCMap.put("174", "117");
        mADToCMap.put("175", "118");
        mADToCMap.put("176", "119");
        mADToCMap.put("177", "120");
        mADToCMap.put("178", "120");
        mADToCMap.put("179", "121");
        mADToCMap.put("180", "122");
        mADToCMap.put("181", "123");
        mADToCMap.put("182", "124");
        mADToCMap.put("183", "124");
        mADToCMap.put("184", "125");
        mADToCMap.put("185", "126");
        mADToCMap.put("186", "126");
        mADToCMap.put("187", "127");
        mADToCMap.put("188", "128");
        mADToCMap.put("189", "129");
        mADToCMap.put("190", "130");
        mADToCMap.put("191", "130");
        mADToCMap.put("192", "131");
        mADToCMap.put("193", "132");
        mADToCMap.put("194", "133");
        mADToCMap.put("195", "134");
        mADToCMap.put("196", "135");
        mADToCMap.put("197", "136");
        mADToCMap.put("198", "137");
        mADToCMap.put("199", "138");
        mADToCMap.put("200", "139");
        mADToCMap.put("201", "140");
        mADToCMap.put("202", "141");
        mADToCMap.put("203", "142");
        mADToCMap.put("204", "143");
        mADToCMap.put("205", "144");
        mADToCMap.put("206", "145");
        mADToCMap.put("207", "146");
        mADToCMap.put("208", "147");
        mADToCMap.put("209", "148");
        mADToCMap.put("210", "149");
        mADToCMap.put("211", "151");
        mADToCMap.put("212", "152");
        mADToCMap.put("213", "153");
        mADToCMap.put("214", "154");
        mADToCMap.put("215", "156");
        mADToCMap.put("216", "157");
        mADToCMap.put("217", "158");
        mADToCMap.put("218", "160");
        mADToCMap.put("219", "161");
        mADToCMap.put("220", "163");
        mADToCMap.put("221", "164");
        mADToCMap.put("222", "166");
        mADToCMap.put("223", "167");
        mADToCMap.put("224", "169");
        mADToCMap.put("225", "171");
        mADToCMap.put("226", "173");
        mADToCMap.put("227", "175");
        mADToCMap.put("228", "177");
        mADToCMap.put("229", "179");
        mADToCMap.put("230", "181");
        mADToCMap.put("231", "184");
        mADToCMap.put("232", "186");
        mADToCMap.put("233", "189");
        mADToCMap.put("234", "191");
        mADToCMap.put("235", "194");
        mADToCMap.put("236", "197");
        mADToCMap.put("237", "199");
        mADToCMap.put("238", "202");
        mADToCMap.put("239", "206");
        mADToCMap.put("240", "209");
        mADToCMap.put("241", "210");
        mADToCMap.put("242", "210");
        mADToCMap.put("243", "210");
        mADToCMap.put("244", "210");
        mADToCMap.put("245", "210");
        mADToCMap.put("246", "210");
        mADToCMap.put("247", "210");
        mADToCMap.put("248", "210");
        mADToCMap.put("249", "210");
        mADToCMap.put("250", "210");
        mADToCMap.put("251", "210");
        mADToCMap.put("252", "210");
        mADToCMap.put("253", "210");
        mADToCMap.put("254", "210");
        mADToCMap.put("255", "210");
    }

    public static String fromCToAD(String c) {
        String ad = mCToADMap.get(c);
        if (ad != null && !ad.isEmpty()) {
            return ad;
        }
        Log.e(TAG, "There is no ad value for the c value of : " + c);
        return c;
    }

    public static String fromADToC(String ad) {
        String c = mADToCMap.get(ad);
        if (c != null && !c.isEmpty()) {
            return c;
        }
        Log.e(TAG, "There is no c value for the ad value of : " + ad);
        return ad;
    }

    public static float fValueFromCValue(float cValue) {
        return ((9.0f * cValue) / 5.0f) + 32.0f;
    }

    public static float cValueFromFValue(float fValue) {
        return ((fValue - 32.0f) * 5.0f) / 9.0f;
    }
}