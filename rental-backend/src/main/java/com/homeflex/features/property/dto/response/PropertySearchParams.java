package com.homeflex.features.property.dto.response;

import java.util.List;

public record PropertySearchParams(
        String city,
        Double minPrice,
        Double maxPrice,
        String propertyType,
        Integer bedrooms,
        Integer bathrooms,
        List<String> amenities
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String city;
        private Double minPrice;
        private Double maxPrice;
        private String propertyType;
        private Integer bedrooms;
        private Integer bathrooms;
        private List<String> amenities;

        public Builder city(String city) { this.city = city; return this; }
        public Builder minPrice(Double minPrice) { this.minPrice = minPrice; return this; }
        public Builder maxPrice(Double maxPrice) { this.maxPrice = maxPrice; return this; }
        public Builder propertyType(String propertyType) { this.propertyType = propertyType; return this; }
        public Builder bedrooms(Integer bedrooms) { this.bedrooms = bedrooms; return this; }
        public Builder bathrooms(Integer bathrooms) { this.bathrooms = bathrooms; return this; }
        public Builder amenities(List<String> amenities) { this.amenities = amenities; return this; }

        public PropertySearchParams build() {
            return new PropertySearchParams(city, minPrice, maxPrice, propertyType, bedrooms, bathrooms, amenities);
        }
    }
}
