// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'vehicle_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$VehicleResponse {

 String get id; String get ownerId; String get brand; String get model; int get year; String get transmission; String get fuelType; double get dailyPrice; String get currency; String get status; String? get description; int? get mileage; int? get seats; String? get color; String? get licensePlate; String? get pickupCity; String? get pickupAddress; int get viewCount; List<VehicleImageDto>? get images; String get createdAt; String get updatedAt;
/// Create a copy of VehicleResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$VehicleResponseCopyWith<VehicleResponse> get copyWith => _$VehicleResponseCopyWithImpl<VehicleResponse>(this as VehicleResponse, _$identity);

  /// Serializes this VehicleResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is VehicleResponse&&(identical(other.id, id) || other.id == id)&&(identical(other.ownerId, ownerId) || other.ownerId == ownerId)&&(identical(other.brand, brand) || other.brand == brand)&&(identical(other.model, model) || other.model == model)&&(identical(other.year, year) || other.year == year)&&(identical(other.transmission, transmission) || other.transmission == transmission)&&(identical(other.fuelType, fuelType) || other.fuelType == fuelType)&&(identical(other.dailyPrice, dailyPrice) || other.dailyPrice == dailyPrice)&&(identical(other.currency, currency) || other.currency == currency)&&(identical(other.status, status) || other.status == status)&&(identical(other.description, description) || other.description == description)&&(identical(other.mileage, mileage) || other.mileage == mileage)&&(identical(other.seats, seats) || other.seats == seats)&&(identical(other.color, color) || other.color == color)&&(identical(other.licensePlate, licensePlate) || other.licensePlate == licensePlate)&&(identical(other.pickupCity, pickupCity) || other.pickupCity == pickupCity)&&(identical(other.pickupAddress, pickupAddress) || other.pickupAddress == pickupAddress)&&(identical(other.viewCount, viewCount) || other.viewCount == viewCount)&&const DeepCollectionEquality().equals(other.images, images)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.updatedAt, updatedAt) || other.updatedAt == updatedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hashAll([runtimeType,id,ownerId,brand,model,year,transmission,fuelType,dailyPrice,currency,status,description,mileage,seats,color,licensePlate,pickupCity,pickupAddress,viewCount,const DeepCollectionEquality().hash(images),createdAt,updatedAt]);

@override
String toString() {
  return 'VehicleResponse(id: $id, ownerId: $ownerId, brand: $brand, model: $model, year: $year, transmission: $transmission, fuelType: $fuelType, dailyPrice: $dailyPrice, currency: $currency, status: $status, description: $description, mileage: $mileage, seats: $seats, color: $color, licensePlate: $licensePlate, pickupCity: $pickupCity, pickupAddress: $pickupAddress, viewCount: $viewCount, images: $images, createdAt: $createdAt, updatedAt: $updatedAt)';
}


}

/// @nodoc
abstract mixin class $VehicleResponseCopyWith<$Res>  {
  factory $VehicleResponseCopyWith(VehicleResponse value, $Res Function(VehicleResponse) _then) = _$VehicleResponseCopyWithImpl;
@useResult
$Res call({
 String id, String ownerId, String brand, String model, int year, String transmission, String fuelType, double dailyPrice, String currency, String status, String? description, int? mileage, int? seats, String? color, String? licensePlate, String? pickupCity, String? pickupAddress, int viewCount, List<VehicleImageDto>? images, String createdAt, String updatedAt
});




}
/// @nodoc
class _$VehicleResponseCopyWithImpl<$Res>
    implements $VehicleResponseCopyWith<$Res> {
  _$VehicleResponseCopyWithImpl(this._self, this._then);

  final VehicleResponse _self;
  final $Res Function(VehicleResponse) _then;

/// Create a copy of VehicleResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? ownerId = null,Object? brand = null,Object? model = null,Object? year = null,Object? transmission = null,Object? fuelType = null,Object? dailyPrice = null,Object? currency = null,Object? status = null,Object? description = freezed,Object? mileage = freezed,Object? seats = freezed,Object? color = freezed,Object? licensePlate = freezed,Object? pickupCity = freezed,Object? pickupAddress = freezed,Object? viewCount = null,Object? images = freezed,Object? createdAt = null,Object? updatedAt = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,ownerId: null == ownerId ? _self.ownerId : ownerId // ignore: cast_nullable_to_non_nullable
as String,brand: null == brand ? _self.brand : brand // ignore: cast_nullable_to_non_nullable
as String,model: null == model ? _self.model : model // ignore: cast_nullable_to_non_nullable
as String,year: null == year ? _self.year : year // ignore: cast_nullable_to_non_nullable
as int,transmission: null == transmission ? _self.transmission : transmission // ignore: cast_nullable_to_non_nullable
as String,fuelType: null == fuelType ? _self.fuelType : fuelType // ignore: cast_nullable_to_non_nullable
as String,dailyPrice: null == dailyPrice ? _self.dailyPrice : dailyPrice // ignore: cast_nullable_to_non_nullable
as double,currency: null == currency ? _self.currency : currency // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,description: freezed == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String?,mileage: freezed == mileage ? _self.mileage : mileage // ignore: cast_nullable_to_non_nullable
as int?,seats: freezed == seats ? _self.seats : seats // ignore: cast_nullable_to_non_nullable
as int?,color: freezed == color ? _self.color : color // ignore: cast_nullable_to_non_nullable
as String?,licensePlate: freezed == licensePlate ? _self.licensePlate : licensePlate // ignore: cast_nullable_to_non_nullable
as String?,pickupCity: freezed == pickupCity ? _self.pickupCity : pickupCity // ignore: cast_nullable_to_non_nullable
as String?,pickupAddress: freezed == pickupAddress ? _self.pickupAddress : pickupAddress // ignore: cast_nullable_to_non_nullable
as String?,viewCount: null == viewCount ? _self.viewCount : viewCount // ignore: cast_nullable_to_non_nullable
as int,images: freezed == images ? _self.images : images // ignore: cast_nullable_to_non_nullable
as List<VehicleImageDto>?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,updatedAt: null == updatedAt ? _self.updatedAt : updatedAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [VehicleResponse].
extension VehicleResponsePatterns on VehicleResponse {
/// A variant of `map` that fallback to returning `orElse`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _VehicleResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _VehicleResponse() when $default != null:
return $default(_that);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// Callbacks receives the raw object, upcasted.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case final Subclass2 value:
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _VehicleResponse value)  $default,){
final _that = this;
switch (_that) {
case _VehicleResponse():
return $default(_that);case _:
  throw StateError('Unexpected subclass');

}
}
/// A variant of `map` that fallback to returning `null`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _VehicleResponse value)?  $default,){
final _that = this;
switch (_that) {
case _VehicleResponse() when $default != null:
return $default(_that);case _:
  return null;

}
}
/// A variant of `when` that fallback to an `orElse` callback.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String ownerId,  String brand,  String model,  int year,  String transmission,  String fuelType,  double dailyPrice,  String currency,  String status,  String? description,  int? mileage,  int? seats,  String? color,  String? licensePlate,  String? pickupCity,  String? pickupAddress,  int viewCount,  List<VehicleImageDto>? images,  String createdAt,  String updatedAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _VehicleResponse() when $default != null:
return $default(_that.id,_that.ownerId,_that.brand,_that.model,_that.year,_that.transmission,_that.fuelType,_that.dailyPrice,_that.currency,_that.status,_that.description,_that.mileage,_that.seats,_that.color,_that.licensePlate,_that.pickupCity,_that.pickupAddress,_that.viewCount,_that.images,_that.createdAt,_that.updatedAt);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// As opposed to `map`, this offers destructuring.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case Subclass2(:final field2):
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String ownerId,  String brand,  String model,  int year,  String transmission,  String fuelType,  double dailyPrice,  String currency,  String status,  String? description,  int? mileage,  int? seats,  String? color,  String? licensePlate,  String? pickupCity,  String? pickupAddress,  int viewCount,  List<VehicleImageDto>? images,  String createdAt,  String updatedAt)  $default,) {final _that = this;
switch (_that) {
case _VehicleResponse():
return $default(_that.id,_that.ownerId,_that.brand,_that.model,_that.year,_that.transmission,_that.fuelType,_that.dailyPrice,_that.currency,_that.status,_that.description,_that.mileage,_that.seats,_that.color,_that.licensePlate,_that.pickupCity,_that.pickupAddress,_that.viewCount,_that.images,_that.createdAt,_that.updatedAt);case _:
  throw StateError('Unexpected subclass');

}
}
/// A variant of `when` that fallback to returning `null`
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String ownerId,  String brand,  String model,  int year,  String transmission,  String fuelType,  double dailyPrice,  String currency,  String status,  String? description,  int? mileage,  int? seats,  String? color,  String? licensePlate,  String? pickupCity,  String? pickupAddress,  int viewCount,  List<VehicleImageDto>? images,  String createdAt,  String updatedAt)?  $default,) {final _that = this;
switch (_that) {
case _VehicleResponse() when $default != null:
return $default(_that.id,_that.ownerId,_that.brand,_that.model,_that.year,_that.transmission,_that.fuelType,_that.dailyPrice,_that.currency,_that.status,_that.description,_that.mileage,_that.seats,_that.color,_that.licensePlate,_that.pickupCity,_that.pickupAddress,_that.viewCount,_that.images,_that.createdAt,_that.updatedAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _VehicleResponse implements VehicleResponse {
  const _VehicleResponse({required this.id, required this.ownerId, required this.brand, required this.model, required this.year, required this.transmission, required this.fuelType, required this.dailyPrice, required this.currency, required this.status, this.description, this.mileage, this.seats, this.color, this.licensePlate, this.pickupCity, this.pickupAddress, required this.viewCount, final  List<VehicleImageDto>? images, required this.createdAt, required this.updatedAt}): _images = images;
  factory _VehicleResponse.fromJson(Map<String, dynamic> json) => _$VehicleResponseFromJson(json);

@override final  String id;
@override final  String ownerId;
@override final  String brand;
@override final  String model;
@override final  int year;
@override final  String transmission;
@override final  String fuelType;
@override final  double dailyPrice;
@override final  String currency;
@override final  String status;
@override final  String? description;
@override final  int? mileage;
@override final  int? seats;
@override final  String? color;
@override final  String? licensePlate;
@override final  String? pickupCity;
@override final  String? pickupAddress;
@override final  int viewCount;
 final  List<VehicleImageDto>? _images;
@override List<VehicleImageDto>? get images {
  final value = _images;
  if (value == null) return null;
  if (_images is EqualUnmodifiableListView) return _images;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(value);
}

@override final  String createdAt;
@override final  String updatedAt;

/// Create a copy of VehicleResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$VehicleResponseCopyWith<_VehicleResponse> get copyWith => __$VehicleResponseCopyWithImpl<_VehicleResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$VehicleResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _VehicleResponse&&(identical(other.id, id) || other.id == id)&&(identical(other.ownerId, ownerId) || other.ownerId == ownerId)&&(identical(other.brand, brand) || other.brand == brand)&&(identical(other.model, model) || other.model == model)&&(identical(other.year, year) || other.year == year)&&(identical(other.transmission, transmission) || other.transmission == transmission)&&(identical(other.fuelType, fuelType) || other.fuelType == fuelType)&&(identical(other.dailyPrice, dailyPrice) || other.dailyPrice == dailyPrice)&&(identical(other.currency, currency) || other.currency == currency)&&(identical(other.status, status) || other.status == status)&&(identical(other.description, description) || other.description == description)&&(identical(other.mileage, mileage) || other.mileage == mileage)&&(identical(other.seats, seats) || other.seats == seats)&&(identical(other.color, color) || other.color == color)&&(identical(other.licensePlate, licensePlate) || other.licensePlate == licensePlate)&&(identical(other.pickupCity, pickupCity) || other.pickupCity == pickupCity)&&(identical(other.pickupAddress, pickupAddress) || other.pickupAddress == pickupAddress)&&(identical(other.viewCount, viewCount) || other.viewCount == viewCount)&&const DeepCollectionEquality().equals(other._images, _images)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.updatedAt, updatedAt) || other.updatedAt == updatedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hashAll([runtimeType,id,ownerId,brand,model,year,transmission,fuelType,dailyPrice,currency,status,description,mileage,seats,color,licensePlate,pickupCity,pickupAddress,viewCount,const DeepCollectionEquality().hash(_images),createdAt,updatedAt]);

@override
String toString() {
  return 'VehicleResponse(id: $id, ownerId: $ownerId, brand: $brand, model: $model, year: $year, transmission: $transmission, fuelType: $fuelType, dailyPrice: $dailyPrice, currency: $currency, status: $status, description: $description, mileage: $mileage, seats: $seats, color: $color, licensePlate: $licensePlate, pickupCity: $pickupCity, pickupAddress: $pickupAddress, viewCount: $viewCount, images: $images, createdAt: $createdAt, updatedAt: $updatedAt)';
}


}

/// @nodoc
abstract mixin class _$VehicleResponseCopyWith<$Res> implements $VehicleResponseCopyWith<$Res> {
  factory _$VehicleResponseCopyWith(_VehicleResponse value, $Res Function(_VehicleResponse) _then) = __$VehicleResponseCopyWithImpl;
@override @useResult
$Res call({
 String id, String ownerId, String brand, String model, int year, String transmission, String fuelType, double dailyPrice, String currency, String status, String? description, int? mileage, int? seats, String? color, String? licensePlate, String? pickupCity, String? pickupAddress, int viewCount, List<VehicleImageDto>? images, String createdAt, String updatedAt
});




}
/// @nodoc
class __$VehicleResponseCopyWithImpl<$Res>
    implements _$VehicleResponseCopyWith<$Res> {
  __$VehicleResponseCopyWithImpl(this._self, this._then);

  final _VehicleResponse _self;
  final $Res Function(_VehicleResponse) _then;

/// Create a copy of VehicleResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? ownerId = null,Object? brand = null,Object? model = null,Object? year = null,Object? transmission = null,Object? fuelType = null,Object? dailyPrice = null,Object? currency = null,Object? status = null,Object? description = freezed,Object? mileage = freezed,Object? seats = freezed,Object? color = freezed,Object? licensePlate = freezed,Object? pickupCity = freezed,Object? pickupAddress = freezed,Object? viewCount = null,Object? images = freezed,Object? createdAt = null,Object? updatedAt = null,}) {
  return _then(_VehicleResponse(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,ownerId: null == ownerId ? _self.ownerId : ownerId // ignore: cast_nullable_to_non_nullable
as String,brand: null == brand ? _self.brand : brand // ignore: cast_nullable_to_non_nullable
as String,model: null == model ? _self.model : model // ignore: cast_nullable_to_non_nullable
as String,year: null == year ? _self.year : year // ignore: cast_nullable_to_non_nullable
as int,transmission: null == transmission ? _self.transmission : transmission // ignore: cast_nullable_to_non_nullable
as String,fuelType: null == fuelType ? _self.fuelType : fuelType // ignore: cast_nullable_to_non_nullable
as String,dailyPrice: null == dailyPrice ? _self.dailyPrice : dailyPrice // ignore: cast_nullable_to_non_nullable
as double,currency: null == currency ? _self.currency : currency // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,description: freezed == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String?,mileage: freezed == mileage ? _self.mileage : mileage // ignore: cast_nullable_to_non_nullable
as int?,seats: freezed == seats ? _self.seats : seats // ignore: cast_nullable_to_non_nullable
as int?,color: freezed == color ? _self.color : color // ignore: cast_nullable_to_non_nullable
as String?,licensePlate: freezed == licensePlate ? _self.licensePlate : licensePlate // ignore: cast_nullable_to_non_nullable
as String?,pickupCity: freezed == pickupCity ? _self.pickupCity : pickupCity // ignore: cast_nullable_to_non_nullable
as String?,pickupAddress: freezed == pickupAddress ? _self.pickupAddress : pickupAddress // ignore: cast_nullable_to_non_nullable
as String?,viewCount: null == viewCount ? _self.viewCount : viewCount // ignore: cast_nullable_to_non_nullable
as int,images: freezed == images ? _self._images : images // ignore: cast_nullable_to_non_nullable
as List<VehicleImageDto>?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,updatedAt: null == updatedAt ? _self.updatedAt : updatedAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
