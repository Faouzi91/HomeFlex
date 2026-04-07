// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'vehicle_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_VehicleResponse _$VehicleResponseFromJson(Map<String, dynamic> json) =>
    _VehicleResponse(
      id: json['id'] as String,
      ownerId: json['ownerId'] as String,
      brand: json['brand'] as String,
      model: json['model'] as String,
      year: (json['year'] as num).toInt(),
      transmission: json['transmission'] as String,
      fuelType: json['fuelType'] as String,
      dailyPrice: (json['dailyPrice'] as num).toDouble(),
      currency: json['currency'] as String,
      status: json['status'] as String,
      description: json['description'] as String?,
      mileage: (json['mileage'] as num?)?.toInt(),
      seats: (json['seats'] as num?)?.toInt(),
      color: json['color'] as String?,
      licensePlate: json['licensePlate'] as String?,
      pickupCity: json['pickupCity'] as String?,
      pickupAddress: json['pickupAddress'] as String?,
      viewCount: (json['viewCount'] as num).toInt(),
      images: (json['images'] as List<dynamic>?)
          ?.map((e) => VehicleImageDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      createdAt: json['createdAt'] as String,
      updatedAt: json['updatedAt'] as String,
    );

Map<String, dynamic> _$VehicleResponseToJson(_VehicleResponse instance) =>
    <String, dynamic>{
      'id': instance.id,
      'ownerId': instance.ownerId,
      'brand': instance.brand,
      'model': instance.model,
      'year': instance.year,
      'transmission': instance.transmission,
      'fuelType': instance.fuelType,
      'dailyPrice': instance.dailyPrice,
      'currency': instance.currency,
      'status': instance.status,
      'description': instance.description,
      'mileage': instance.mileage,
      'seats': instance.seats,
      'color': instance.color,
      'licensePlate': instance.licensePlate,
      'pickupCity': instance.pickupCity,
      'pickupAddress': instance.pickupAddress,
      'viewCount': instance.viewCount,
      'images': instance.images,
      'createdAt': instance.createdAt,
      'updatedAt': instance.updatedAt,
    };
