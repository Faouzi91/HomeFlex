package com.homeflex.features.property.domain.enums;

public enum PropertyType {
    APARTMENT,
    HOUSE,
    STUDIO,
    VILLA,
    ROOM,
    OFFICE,
    LAND,
    HOTEL,
    GUESTHOUSE,
    HOSTEL,
    RESORT;

    public boolean isHotelType() {
        return this == HOTEL || this == GUESTHOUSE || this == HOSTEL || this == RESORT;
    }
}

