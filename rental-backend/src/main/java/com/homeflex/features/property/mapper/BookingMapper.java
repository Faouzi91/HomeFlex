package com.homeflex.features.property.mapper;

import com.homeflex.core.mapper.UserMapper;

import com.homeflex.features.property.dto.response.BookingDto;
import com.homeflex.features.property.domain.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PropertyMapper.class, UserMapper.class})
public interface BookingMapper {

    @Mapping(target = "bookingType", expression = "java(booking.getBookingType() != null ? booking.getBookingType().name() : null)")
    @Mapping(target = "status", expression = "java(booking.getStatus() != null ? booking.getStatus().name() : null)")
    @Mapping(target = "stripeClientSecret", source = "stripeClientSecret")
    @Mapping(target = "paymentStatus", source = "paymentStatus")
    @Mapping(target = "paymentFailureReason", source = "paymentFailureReason")
    @Mapping(target = "roomTypeId", expression = "java(booking.getRoomType() != null ? booking.getRoomType().getId() : null)")
    @Mapping(target = "roomTypeName", expression = "java(booking.getRoomType() != null ? booking.getRoomType().getName() : null)")
    @Mapping(target = "numberOfRooms", source = "numberOfRooms")
    @Mapping(target = "unitId", expression = "java(booking.getUnit() != null ? booking.getUnit().getId() : null)")
    @Mapping(target = "unitNumber", expression = "java(booking.getUnit() != null ? booking.getUnit().getUnitNumber() : null)")
    @Mapping(target = "paymentConfirmedAt", source = "paymentConfirmedAt")
    @Mapping(target = "escrowReleasedAt", source = "escrowReleasedAt")
    BookingDto toDto(Booking booking);

    List<BookingDto> toDto(List<Booking> bookings);
}
