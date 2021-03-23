package com.mbld.jigsly.enumeration;

public enum UserColor {
    COLOR_1("0x5C8FC2"),
    COLOR_2("0xBC5B5F"),
    COLOR_3("0x60BD68"),
    COLOR_4("0x8561A9"),
    COLOR_5("0xB466A9"),
    COLOR_6("0x5BBFC0"),
    COLOR_7("0xBD8561"),
    COLOR_8("0xBABA5F"),
    COLOR_9("0x734A23"),
    COLOR_10("0x777877"),
    ;

    private final String colorCode;
    UserColor(String colorCode){
        this.colorCode = colorCode;
    }

    public String getColorCode() {
        return colorCode;
    }
}
