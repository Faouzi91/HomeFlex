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
    BookingDto toDto(Booking booking);

    List<BookingDto> toDto(List<Booking> bookings);
}
