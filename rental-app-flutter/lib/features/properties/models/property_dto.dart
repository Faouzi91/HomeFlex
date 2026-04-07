import 'package:freezed_annotation/freezed_annotation.dart';
import '../../auth/models/user_dto.dart';
import 'amenity_dto.dart';
import 'property_image_dto.dart';
import 'property_video_dto.dart';

part 'property_dto.freezed.dart';
part 'property_dto.g.dart';

@freezed
abstract class PropertyDto with _$PropertyDto {
  const factory PropertyDto({
    required String id,
    required String title,
    required String description,
    required String propertyType,
    required String listingType,
    required double price,
    required String currency,
    required String address,
    required String city,
    required String stateProvince,
    required String country,
    String? postalCode,
    double? latitude,
    double? longitude,
    int? bedrooms,
    int? bathrooms,
    double? areaSqm,
    int? floorNumber,
    int? totalFloors,
    required bool isAvailable,
    String? availableFrom,
    required String status,
    int? viewCount,
    int? favoriteCount,
    List<PropertyImageDto>? images,
    List<PropertyVideoDto>? videos,
    List<AmenityDto>? amenities,
    UserDto? landlord,
    required String createdAt,
    required String updatedAt,
  }) = _PropertyDto;

  factory PropertyDto.fromJson(Map<String, dynamic> json) => _$PropertyDtoFromJson(json);
}
