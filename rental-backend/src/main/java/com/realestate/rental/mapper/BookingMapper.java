package com.realestate.rental.mapper;

import com.realestate.rental.dto.response.BookingDto;
import com.realestate.rental.domain.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PropertyMapper.class, UserMapper.class})
public interface BookingMapper {

    @Mapping(target = "bookingType", expression = "java(booking.getBookingType() != null ? booking.getBookingType().name() : null)")
    @Mapping(target = "status", expression = "java(booking.getStatus() != null ? booking.getStatus().name() : null)")
    BookingDto toDto(Booking booking);

    List<BookingDto> toDto(List<Booking> bookings);
}
