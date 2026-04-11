// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'property_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_PropertyDto _$PropertyDtoFromJson(Map<String, dynamic> json) => _PropertyDto(
  id: json['id'] as String,
  title: json['title'] as String,
  description: json['description'] as String,
  propertyType: json['propertyType'] as String,
  listingType: json['listingType'] as String,
  price: (json['price'] as num).toDouble(),
  currency: json['currency'] as String,
  address: json['address'] as String,
  city: json['city'] as String,
  stateProvince: json['stateProvince'] as String,
  country: json['country'] as String,
  postalCode: json['postalCode'] as String?,
  latitude: (json['latitude'] as num?)?.toDouble(),
  longitude: (json['longitude'] as num?)?.toDouble(),
  bedrooms: (json['bedrooms'] as num?)?.toInt(),
  bathrooms: (json['bathrooms'] as num?)?.toInt(),
  areaSqm: (json['areaSqm'] as num?)?.toDouble(),
  floorNumber: (json['floorNumber'] as num?)?.toInt(),
  totalFloors: (json['totalFloors'] as num?)?.toInt(),
  agencyId: json['agencyId'] as String?,
  isAvailable: json['isAvailable'] as bool,
  availableFrom: json['availableFrom'] as String?,
  status: json['status'] as String,
  viewCount: (json['viewCount'] as num?)?.toInt(),
  favoriteCount: (json['favoriteCount'] as num?)?.toInt(),
  images: (json['images'] as List<dynamic>?)
      ?.map((e) => PropertyImageDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  videos: (json['videos'] as List<dynamic>?)
      ?.map((e) => PropertyVideoDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  amenities: (json['amenities'] as List<dynamic>?)
      ?.map((e) => AmenityDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  landlord: json['landlord'] == null
      ? null
      : UserDto.fromJson(json['landlord'] as Map<String, dynamic>),
  createdAt: json['createdAt'] as String,
  updatedAt: json['updatedAt'] as String,
);

Map<String, dynamic> _$PropertyDtoToJson(_PropertyDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'title': instance.title,
      'description': instance.description,
      'propertyType': instance.propertyType,
      'listingType': instance.listingType,
      'price': instance.price,
      'currency': instance.currency,
      'address': instance.address,
      'city': instance.city,
      'stateProvince': instance.stateProvince,
      'country': instance.country,
      'postalCode': instance.postalCode,
      'latitude': instance.latitude,
      'longitude': instance.longitude,
      'bedrooms': instance.bedrooms,
      'bathrooms': instance.bathrooms,
      'areaSqm': instance.areaSqm,
      'floorNumber': instance.floorNumber,
      'totalFloors': instance.totalFloors,
      'agencyId': instance.agencyId,
      'isAvailable': instance.isAvailable,
      'availableFrom': instance.availableFrom,
      'status': instance.status,
      'viewCount': instance.viewCount,
      'favoriteCount': instance.favoriteCount,
      'images': instance.images,
      'videos': instance.videos,
      'amenities': instance.amenities,
      'landlord': instance.landlord,
      'createdAt': instance.createdAt,
      'updatedAt': instance.updatedAt,
    };
