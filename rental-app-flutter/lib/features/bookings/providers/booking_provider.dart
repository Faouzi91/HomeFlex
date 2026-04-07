import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/booking_dto.dart';
import '../../../core/api/api_client.dart';

final myBookingsProvider = FutureProvider<List<BookingDto>>((ref) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/bookings/my-bookings');
  final List<dynamic> data = response.data['data'];
  return data.map((json) => BookingDto.fromJson(json)).toList();
});

final propertyBookingsProvider =
    FutureProvider.family<List<BookingDto>, String>((ref, propertyId) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/bookings/property/$propertyId');
  final List<dynamic> data = response.data['data'];
  return data.map((json) => BookingDto.fromJson(json)).toList();
});

final bookingDetailProvider =
    FutureProvider.family<BookingDto, String>((ref, id) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/bookings/$id');
  return BookingDto.fromJson(response.data);
});

class BookingNotifier {
  final _apiClient = ApiClient();

  Future<BookingDto> createBooking({
    required String propertyId,
    required String bookingType,
    String? startDate,
    String? endDate,
    String? message,
    int? numberOfOccupants,
  }) async {
    final response = await _apiClient.dio.post('/bookings', data: {
      'propertyId': propertyId,
      'bookingType': bookingType,
      'startDate': ?startDate,
      'endDate': ?endDate,
      'message': ?message,
      'numberOfOccupants': ?numberOfOccupants,
    });
    return BookingDto.fromJson(response.data);
  }

  Future<BookingDto> approveBooking(String id, {String? message}) async {
    final response = await _apiClient.dio.patch('/bookings/$id/approve',
        data: message != null ? {'message': message} : null);
    return BookingDto.fromJson(response.data);
  }

  Future<BookingDto> rejectBooking(String id, {String? message}) async {
    final response = await _apiClient.dio.patch('/bookings/$id/reject',
        data: message != null ? {'message': message} : null);
    return BookingDto.fromJson(response.data);
  }

  Future<BookingDto> cancelBooking(String id) async {
    final response = await _apiClient.dio.patch('/bookings/$id/cancel');
    return BookingDto.fromJson(response.data);
  }
}

final bookingNotifierProvider = Provider((ref) => BookingNotifier());
