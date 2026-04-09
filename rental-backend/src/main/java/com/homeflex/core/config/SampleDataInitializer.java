// ====================================
// SampleDataInitializer.java
// Creates realistic sample data for demo
// ====================================
package com.homeflex.core.config;

import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.features.property.domain.repository.PropertyImageRepository;
import com.homeflex.features.property.domain.repository.AmenityRepository;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.features.property.domain.repository.FavoriteRepository;
import com.homeflex.features.property.domain.repository.ReviewRepository;
import com.homeflex.features.vehicle.domain.repository.VehicleRepository;
import com.homeflex.features.vehicle.domain.repository.VehicleImageRepository;
import com.homeflex.features.vehicle.domain.repository.VehicleBookingRepository;
import com.homeflex.core.domain.entity.User;
import com.homeflex.features.property.domain.entity.Amenity;
import com.homeflex.features.property.domain.entity.Booking;
import com.homeflex.features.property.domain.entity.Favorite;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.entity.PropertyImage;
import com.homeflex.features.property.domain.entity.Review;
import com.homeflex.features.vehicle.domain.entity.Vehicle;
import com.homeflex.features.vehicle.domain.entity.VehicleImage;
import com.homeflex.features.vehicle.domain.entity.VehicleBooking;
import com.homeflex.features.property.domain.enums.AmenityCategory;
import com.homeflex.features.property.domain.enums.BookingStatus;
import com.homeflex.features.property.domain.enums.BookingType;
import com.homeflex.features.property.domain.enums.ListingType;
import com.homeflex.features.property.domain.enums.PropertyStatus;
import com.homeflex.features.property.domain.enums.PropertyType;
import com.homeflex.features.vehicle.domain.enums.VehicleStatus;
import com.homeflex.features.vehicle.domain.enums.Transmission;
import com.homeflex.features.vehicle.domain.enums.FuelType;
import com.homeflex.features.vehicle.domain.enums.VehicleBookingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class SampleDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final AmenityRepository amenityRepository;
    private final BookingRepository bookingRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleImageRepository vehicleImageRepository;
    private final VehicleBookingRepository vehicleBookingRepository;

    @Value("${app.data.create-sample-properties:true}")
    private boolean createSampleProperties;

    @Override
    @Transactional
    public void run(String... args) {
        if (!createSampleProperties) {
            log.info(" Sample data creation is disabled");
            return;
        }

        if (propertyRepository.count() > 0 || vehicleRepository.count() > 0) {
            log.info(" Sample data already exists, skipping...");
            return;
        }

        log.info("Creating sample data...");

        try {
            createAmenities();
            createSampleProperties();
            createSampleVehicles();
            createSampleBookings();
            createSampleFavorites();
            createSampleReviews();

            log.info("═══════════════════════════════════════════════════════");
            log.info("SAMPLE DATA CREATED SUCCESSFULLY!");
            log.info("═══════════════════════════════════════════════════════");
            log.info("Statistics:");
            log.info("   - Properties: {}", propertyRepository.count());
            log.info("   - Vehicles:   {}", vehicleRepository.count());
            log.info("   - Bookings:   {}", bookingRepository.count());
            log.info("   - Favorites:  {}", favoriteRepository.count());
            log.info("   - Reviews:    {}", reviewRepository.count());
            log.info("═══════════════════════════════════════════════════════");
        } catch (Exception e) {
            log.error(" Failed to create sample data: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void createAmenities() {
        if (amenityRepository.count() > 0) {
            return;
        }

        log.info("  → Creating amenities...");

        createAmenity("WiFi", "WiFi", "wifi", AmenityCategory.BASIC);
        createAmenity("Air Conditioning", "Climatisation", "wind", AmenityCategory.COMFORT);
        createAmenity("Heating", "Chauffage", "thermometer", AmenityCategory.COMFORT);
        createAmenity("Parking", "Parking", "car", AmenityCategory.BASIC);
        createAmenity("Elevator", "Ascenseur", "arrow-up", AmenityCategory.BASIC);
        createAmenity("Balcony", "Balcon", "home", AmenityCategory.OUTDOOR);
        createAmenity("Garden", "Jardin", "tree", AmenityCategory.OUTDOOR);
        createAmenity("Swimming Pool", "Piscine", "waves", AmenityCategory.OUTDOOR);
        createAmenity("Security", "Sécurité", "shield", AmenityCategory.SAFETY);
        createAmenity("Furnished", "Meublé", "sofa", AmenityCategory.COMFORT);
        createAmenity("Kitchen", "Cuisine", "utensils", AmenityCategory.BASIC);
        createAmenity("Washing Machine", "Machine à laver", "washing-machine", AmenityCategory.BASIC);
        createAmenity("Dishwasher", "Lave-vaisselle", "dishwasher", AmenityCategory.COMFORT);
        createAmenity("TV", "Télévision", "tv", AmenityCategory.COMFORT);
        createAmenity("Pet Friendly", "Animaux acceptés", "paw", AmenityCategory.OTHER);
        createAmenity("Gym", "Salle de sport", "dumbbell", AmenityCategory.COMFORT);
        createAmenity("Concierge", "Concierge", "user", AmenityCategory.COMFORT);
        createAmenity("Storage", "Rangement", "box", AmenityCategory.OTHER);
    }

    private void createAmenity(String name, String nameFr, String icon, AmenityCategory category) {
        Amenity amenity = new Amenity();
        amenity.setName(name);
        amenity.setNameFr(nameFr);
        amenity.setIcon(icon);
        amenity.setCategory(category);
        amenityRepository.save(amenity);
    }

    private void createSampleProperties() {
        log.info("  → Creating sample properties...");

        User landlord = userRepository.findByEmail("landlord@test.com")
                .orElseThrow(() -> new RuntimeException("Landlord not found"));

        List<Amenity> allAmenities = amenityRepository.findAll();

        // Property 1: Luxury Apartment in Douala
        createProperty(
                landlord,
                "Luxury 3BR Apartment - Bonanjo, Douala",
                "Stunning modern apartment in the heart of Bonanjo. This spacious 3-bedroom apartment features floor-to-ceiling windows with breathtaking city views, a modern kitchen with high-end appliances, and luxurious finishes throughout. Perfect for executives and families.",
                PropertyType.APARTMENT,
                ListingType.RENT,
                new BigDecimal("350000"),
                "Bonanjo, Rue Joffre",
                "Douala",
                "Littoral",
                "Cameroon",
                4.0511, 9.7679,
                3, 2, new BigDecimal("135"),
                5, 10,
                Arrays.asList("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267",
                        "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688",
                        "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2"),
                getRandomAmenities(allAmenities, 10)
        );

        // Property 2: Cozy Studio in Yaoundé
        createProperty(
                landlord,
                "Modern Studio - Bastos, Yaoundé",
                "Charming studio apartment in upscale Bastos neighborhood. Features an open-plan living space, modern bathroom, and a small kitchenette. Ideal for young professionals or students. Walking distance to restaurants and shops.",
                PropertyType.STUDIO,
                ListingType.RENT,
                new BigDecimal("120000"),
                "Bastos, Avenue Kennedy",
                "Yaoundé",
                "Centre",
                "Cameroon",
                3.8480, 11.5021,
                1, 1, new BigDecimal("45"),
                3, 5,
                Arrays.asList("https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af",
                        "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2"),
                getRandomAmenities(allAmenities, 6)
        );

        // Property 3: Family House in Douala
        createProperty(
                landlord,
                "Spacious 4BR Family House - Bonapriso",
                "Beautiful family home in quiet Bonapriso neighborhood. Features 4 bedrooms, 3 bathrooms, large living room, modern kitchen, and a private garden. Perfect for families seeking comfort and space.",
                PropertyType.HOUSE,
                ListingType.RENT,
                new BigDecimal("500000"),
                "Bonapriso, Rue des Palmiers",
                "Douala",
                "Littoral",
                "Cameroon",
                4.0469, 9.7163,
                4, 3, new BigDecimal("200"),
                null, null,
                Arrays.asList("https://images.unsplash.com/photo-1568605114967-8130f3a36994",
                        "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9"),
                getRandomAmenities(allAmenities, 12)
        );

        // Property 4: Executive Villa
        createProperty(
                landlord,
                "Executive Villa with Pool - Yaoundé",
                "Luxurious 5-bedroom villa in prestigious area. Features include swimming pool, landscaped garden, staff quarters, and garage for 2 cars. High-security compound with 24/7 security.",
                PropertyType.VILLA,
                ListingType.RENT,
                new BigDecimal("800000"),
                "Golf Quarter, Boulevard du 20 Mai",
                "Yaoundé",
                "Centre",
                "Cameroon",
                3.8667, 11.5167,
                5, 4, new BigDecimal("350"),
                null, null,
                Arrays.asList("https://images.unsplash.com/photo-1613490493576-7fde63acd811",
                        "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c"),
                getRandomAmenities(allAmenities, 15)
        );

        // Property 5: Budget-friendly Room
        createProperty(
                landlord,
                "Affordable Room in Shared Apartment",
                "Clean and comfortable room in a shared 3-bedroom apartment. Shared kitchen and bathroom. Utilities included. Great for students and young professionals on a budget.",
                PropertyType.ROOM,
                ListingType.RENT,
                new BigDecimal("60000"),
                "Makepe, Rue du Commerce",
                "Douala",
                "Littoral",
                "Cameroon",
                4.0614, 9.7399,
                1, null, new BigDecimal("15"),
                2, 4,
                Arrays.asList("https://images.unsplash.com/photo-1540518614846-7eded433c457"),
                getRandomAmenities(allAmenities, 4)
        );

        // Property 6: Commercial Office Space
        createProperty(
                landlord,
                "Modern Office Space - CBD Douala",
                "Prime office space in central business district. 150 sqm open-plan layout with meeting rooms, reception area, and kitchenette. High-speed internet and parking included.",
                PropertyType.OFFICE,
                ListingType.RENT,
                new BigDecimal("400000"),
                "Akwa, Boulevard de la Liberté",
                "Douala",
                "Littoral",
                "Cameroon",
                4.0435, 9.6999,
                null, 2, new BigDecimal("150"),
                4, 8,
                Arrays.asList("https://images.unsplash.com/photo-1497366216548-37526070297c"),
                getRandomAmenities(allAmenities, 3)
        );

        // Property 7: Beachfront Apartment
        createProperty(
                landlord,
                "Beachfront 2BR Apartment - Limbe",
                "Wake up to ocean views! Modern 2-bedroom apartment right on Limbe beach. Features large balcony overlooking the sea, modern amenities, and resort-style living.",
                PropertyType.APARTMENT,
                ListingType.SHORT_TERM,
                new BigDecimal("180000"),
                "Mile 4 Beach, Limbe",
                "Limbe",
                "Sud-Ouest",
                "Cameroon",
                4.0175, 9.2067,
                2, 2, new BigDecimal("90"),
                1, 3,
                Arrays.asList("https://images.unsplash.com/photo-1571896349842-33c89424de2d"),
                getRandomAmenities(allAmenities, 8)
        );

        // Property 8: Student Housing
        createProperty(
                landlord,
                "Student Studio near University",
                "Perfect for students! Compact studio near university campus. Includes bed, desk, wardrobe, and small kitchen. Quiet study environment.",
                PropertyType.STUDIO,
                ListingType.RENT,
                new BigDecimal("80000"),
                "Ngoa Ekelle, Yaoundé",
                "Yaoundé",
                "Centre",
                "Cameroon",
                3.8580, 11.5210,
                1, 1, new BigDecimal("30"),
                1, 3,
                Arrays.asList("https://images.unsplash.com/photo-1555854877-bab0e564b8d5"),
                getRandomAmenities(allAmenities, 5)
        );

        log.info("  ✓ Created {} properties", propertyRepository.count());
    }

    private void createProperty(User landlord, String title, String description,
                                PropertyType type, ListingType listingType, BigDecimal price,
                                String address, String city, String state, String country,
                                double lat, double lon,
                                Integer bedrooms, Integer bathrooms, BigDecimal area,
                                Integer floor, Integer totalFloors,
                                List<String> imageUrls, List<Amenity> amenities) {

        Property property = new Property();
        property.setLandlord(landlord);
        property.setTitle(title);
        property.setDescription(description);
        property.setPropertyType(type);
        property.setListingType(listingType);
        property.setPrice(price);
        property.setCurrency("XAF");
        property.setAddress(address);
        property.setCity(city);
        property.setStateProvince(state);
        property.setCountry(country);
        property.setLatitude(BigDecimal.valueOf(lat));
        property.setLongitude(BigDecimal.valueOf(lon));
        property.setBedrooms(bedrooms);
        property.setBathrooms(bathrooms);
        property.setAreaSqm(area);
        property.setFloorNumber(floor);
        property.setTotalFloors(totalFloors);
        property.setIsAvailable(true);
        property.setAvailableFrom(LocalDate.now().plusDays(7));
        property.setStatus(PropertyStatus.APPROVED);
        property.setViewCount(new Random().nextInt(100));
        property.setFavoriteCount(0);
        property.setCreatedAt(LocalDateTime.now());

        if (amenities != null && !amenities.isEmpty()) {
            property.setAmenities(new HashSet<>(amenities));
        }

        if (imageUrls != null) {
            for (int i = 0; i < imageUrls.size(); i++) {
                PropertyImage image = new PropertyImage();
                image.setImageUrl(imageUrls.get(i));
                image.setDisplayOrder(i);
                image.setIsPrimary(i == 0);
                image.setProperty(property);
                property.getImages().add(image);
            }
        }

        propertyRepository.save(property);
    }

    private void createSampleVehicles() {
        log.info("  → Creating sample vehicles...");

        User landlord = userRepository.findByEmail("landlord@test.com")
                .orElseThrow(() -> new RuntimeException("Landlord not found"));

        // Vehicle 1: BMW X5
        createVehicle(
                landlord,
                "BMW", "X5", 2023,
                "Luxury SUV perfect for business trips or family travel. Excellent performance and comfort.",
                Transmission.AUTO, FuelType.GASOLINE,
                new BigDecimal("75000"), "XAF",
                "Akwa, Douala", "Douala",
                5, 25000, "Black", "LT-123-AB",
                Arrays.asList("https://images.unsplash.com/photo-1555215695-3004980ad54e")
        );

        // Vehicle 2: Toyota Hilux
        createVehicle(
                landlord,
                "Toyota", "Hilux", 2022,
                "Robust 4x4 pickup truck. Ideal for rough terrain and long distance travel across the country.",
                Transmission.MANUAL, FuelType.DIESEL,
                new BigDecimal("60000"), "XAF",
                "Bastos, Yaoundé", "Yaoundé",
                5, 45000, "White", "CE-456-XY",
                Arrays.asList("https://images.unsplash.com/photo-1583121274602-3e2820c69888")
        );

        // Vehicle 3: Tesla Model 3
        createVehicle(
                landlord,
                "Tesla", "Model 3", 2024,
                "Modern electric sedan. High tech, sustainable and very comfortable for city driving.",
                Transmission.AUTO, FuelType.ELECTRIC,
                new BigDecimal("90000"), "XAF",
                "Bonapriso, Douala", "Douala",
                5, 5000, "Blue", "LT-789-EL",
                Arrays.asList("https://images.unsplash.com/photo-1560958089-b8a1929cea89")
        );

        log.info("  ✓ Created {} vehicles", vehicleRepository.count());
    }

    private void createVehicle(User owner, String brand, String model, int year,
                               String description, Transmission transmission, FuelType fuel,
                               BigDecimal price, String currency, String address, String city,
                               int seats, int mileage, String color, String plate,
                               List<String> imageUrls) {

        Vehicle vehicle = new Vehicle();
        vehicle.setOwnerId(owner.getId());
        vehicle.setBrand(brand);
        vehicle.setModel(model);
        vehicle.setYear(year);
        vehicle.setDescription(description);
        vehicle.setTransmission(transmission);
        vehicle.setFuelType(fuel);
        vehicle.setDailyPrice(price);
        vehicle.setCurrency(currency);
        vehicle.setPickupAddress(address);
        vehicle.setPickupCity(city);
        vehicle.setSeats(seats);
        vehicle.setMileage(mileage);
        vehicle.setColor(color);
        vehicle.setLicensePlate(plate);
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setUpdatedAt(LocalDateTime.now());

        if (imageUrls != null) {
            for (int i = 0; i < imageUrls.size(); i++) {
                VehicleImage image = new VehicleImage();
                image.setImageUrl(imageUrls.get(i));
                image.setDisplayOrder(i);
                image.setPrimary(i == 0);
                image.setVehicle(vehicle);
                vehicle.getImages().add(image);
            }
        }

        vehicleRepository.save(vehicle);
    }

    private List<Amenity> getRandomAmenities(List<Amenity> allAmenities, int count) {
        List<Amenity> shuffled = new ArrayList<>(allAmenities);
        Collections.shuffle(shuffled);
        return shuffled.stream().limit(Math.min(count, shuffled.size())).toList();
    }

    private void createSampleBookings() {
        log.info("  → Creating sample bookings...");

        User tenant = userRepository.findByEmail("tenant@test.com").orElse(null);
        if (tenant == null) return;

        List<Property> properties = propertyRepository.findAll();
        List<Vehicle> vehicles = vehicleRepository.findAll();

        // Property bookings
        for (int i = 0; i < Math.min(5, properties.size()); i++) {
            Property property = properties.get(i);
            Booking booking = new Booking();
            booking.setProperty(property);
            booking.setTenant(tenant);
            booking.setBookingType(i % 2 == 0 ? BookingType.VIEWING : BookingType.RENTAL);
            booking.setRequestedDate(LocalDateTime.now().plusDays(i + 1));
            booking.setStartDate(LocalDate.now().plusDays(i + 7));
            booking.setEndDate(LocalDate.now().plusDays(i + 37));
            booking.setMessage("I'm interested in this property. Can we schedule a viewing?");
            booking.setNumberOfOccupants(2);
            booking.setStatus(i % 3 == 0 ? BookingStatus.APPROVED : (i % 3 == 1 ? BookingStatus.PENDING : BookingStatus.REJECTED));
            bookingRepository.save(booking);
        }

        // Vehicle bookings
        for (int i = 0; i < Math.min(3, vehicles.size()); i++) {
            Vehicle vehicle = vehicles.get(i);
            VehicleBooking booking = new VehicleBooking();
            booking.setVehicleId(vehicle.getId());
            booking.setTenantId(tenant.getId());
            booking.setStartDate(LocalDate.now().plusDays(i + 2));
            booking.setEndDate(LocalDate.now().plusDays(i + 5));
            booking.setTotalPrice(vehicle.getDailyPrice().multiply(new BigDecimal("3")));
            booking.setCurrency(vehicle.getCurrency());
            booking.setStatus(i % 2 == 0 ? VehicleBookingStatus.CONFIRMED : VehicleBookingStatus.PENDING);
            booking.setCreatedAt(LocalDateTime.now());
            vehicleBookingRepository.save(booking);
        }

        log.info("  ✓ Created sample bookings");
    }

    private void createSampleFavorites() {
        log.info("  → Creating sample favorites...");

        User tenant = userRepository.findByEmail("tenant@test.com").orElse(null);
        if (tenant == null) return;

        List<Property> properties = propertyRepository.findAll();
        for (int i = 0; i < Math.min(3, properties.size()); i++) {
            Favorite favorite = new Favorite();
            favorite.setUser(tenant);
            favorite.setProperty(properties.get(i));
            favoriteRepository.save(favorite);
            Property p = properties.get(i);
            p.setFavoriteCount(p.getFavoriteCount() + 1);
            propertyRepository.save(p);
        }
    }

    private void createSampleReviews() {
        log.info("  → Creating sample reviews...");

        User tenant = userRepository.findByEmail("tenant@test.com").orElse(null);
        if (tenant == null) return;

        List<Property> properties = propertyRepository.findAll();
        String[] comments = {
                "Amazing property! The landlord was very responsive and professional.",
                "Great location and the apartment is exactly as described.",
                "Good value for money. The property is clean and well-maintained."
        };

        for (int i = 0; i < Math.min(3, properties.size()); i++) {
            Review review = new Review();
            review.setProperty(properties.get(i));
            review.setReviewer(tenant);
            review.setRating(4 + (i % 2));
            review.setComment(comments[i % comments.length]);
            reviewRepository.save(review);
        }
    }
}
