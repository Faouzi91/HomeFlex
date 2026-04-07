// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'property_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$PropertyDto {

 String get id; String get title; String get description; String get propertyType; String get listingType; double get price; String get currency; String get address; String get city; String get stateProvince; String get country; String? get postalCode; double? get latitude; double? get longitude; int? get bedrooms; int? get bathrooms; double? get areaSqm; int? get floorNumber; int? get totalFloors; bool get isAvailable; String? get availableFrom; String get status; int? get viewCount; int? get favoriteCount; List<PropertyImageDto>? get images; List<PropertyVideoDto>? get videos; List<AmenityDto>? get amenities; UserDto? get landlord; String get createdAt; String get updatedAt;
/// Create a copy of PropertyDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PropertyDtoCopyWith<PropertyDto> get copyWith => _$PropertyDtoCopyWithImpl<PropertyDto>(this as PropertyDto, _$identity);

  /// Serializes this PropertyDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PropertyDto&&(identical(other.id, id) || other.id == id)&&(identical(other.title, title) || other.title == title)&&(identical(other.description, description) || other.description == description)&&(identical(other.propertyType, propertyType) || other.propertyType == propertyType)&&(identical(other.listingType, listingType) || other.listingType == listingType)&&(identical(other.price, price) || other.price == price)&&(identical(other.currency, currency) || other.currency == currency)&&(identical(other.address, address) || other.address == address)&&(identical(other.city, city) || other.city == city)&&(identical(other.stateProvince, stateProvince) || other.stateProvince == stateProvince)&&(identical(other.country, country) || other.country == country)&&(identical(other.postalCode, postalCode) || other.postalCode == postalCode)&&(identical(other.latitude, latitude) || other.latitude == latitude)&&(identical(other.longitude, longitude) || other.longitude == longitude)&&(identical(other.bedrooms, bedrooms) || other.bedrooms == bedrooms)&&(identical(other.bathrooms, bathrooms) || other.bathrooms == bathrooms)&&(identical(other.areaSqm, areaSqm) || other.areaSqm == areaSqm)&&(identical(other.floorNumber, floorNumber) || other.floorNumber == floorNumber)&&(identical(other.totalFloors, totalFloors) || other.totalFloors == totalFloors)&&(identical(other.isAvailable, isAvailable) || other.isAvailable == isAvailable)&&(identical(other.availableFrom, availableFrom) || other.availableFrom == availableFrom)&&(identical(other.status, status) || other.status == status)&&(identical(other.viewCount, viewCount) || other.viewCount == viewCount)&&(identical(other.favoriteCount, favoriteCount) || other.favoriteCount == favoriteCount)&&const DeepCollectionEquality().equals(other.images, images)&&const DeepCollectionEquality().equals(other.videos, videos)&&const DeepCollectionEquality().equals(other.amenities, amenities)&&(identical(other.landlord, landlord) || other.landlord == landlord)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.updatedAt, updatedAt) || other.updatedAt == updatedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hashAll([runtimeType,id,title,description,propertyType,listingType,price,currency,address,city,stateProvince,country,postalCode,latitude,longitude,bedrooms,bathrooms,areaSqm,floorNumber,totalFloors,isAvailable,availableFrom,status,viewCount,favoriteCount,const DeepCollectionEquality().hash(images),const DeepCollectionEquality().hash(videos),const DeepCollectionEquality().hash(amenities),landlord,createdAt,updatedAt]);

@override
String toString() {
  return 'PropertyDto(id: $id, title: $title, description: $description, propertyType: $propertyType, listingType: $listingType, price: $price, currency: $currency, address: $address, city: $city, stateProvince: $stateProvince, country: $country, postalCode: $postalCode, latitude: $latitude, longitude: $longitude, bedrooms: $bedrooms, bathrooms: $bathrooms, areaSqm: $areaSqm, floorNumber: $floorNumber, totalFloors: $totalFloors, isAvailable: $isAvailable, availableFrom: $availableFrom, status: $status, viewCount: $viewCount, favoriteCount: $favoriteCount, images: $images, videos: $videos, amenities: $amenities, landlord: $landlord, createdAt: $createdAt, updatedAt: $updatedAt)';
}


}

/// @nodoc
abstract mixin class $PropertyDtoCopyWith<$Res>  {
  factory $PropertyDtoCopyWith(PropertyDto value, $Res Function(PropertyDto) _then) = _$PropertyDtoCopyWithImpl;
@useResult
$Res call({
 String id, String title, String description, String propertyType, String listingType, double price, String currency, String address, String city, String stateProvince, String country, String? postalCode, double? latitude, double? longitude, int? bedrooms, int? bathrooms, double? areaSqm, int? floorNumber, int? totalFloors, bool isAvailable, String? availableFrom, String status, int? viewCount, int? favoriteCount, List<PropertyImageDto>? images, List<PropertyVideoDto>? videos, List<AmenityDto>? amenities, UserDto? landlord, String createdAt, String updatedAt
});


$UserDtoCopyWith<$Res>? get landlord;

}
/// @nodoc
class _$PropertyDtoCopyWithImpl<$Res>
    implements $PropertyDtoCopyWith<$Res> {
  _$PropertyDtoCopyWithImpl(this._self, this._then);

  final PropertyDto _self;
  final $Res Function(PropertyDto) _then;

/// Create a copy of PropertyDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? title = null,Object? description = null,Object? propertyType = null,Object? listingType = null,Object? price = null,Object? currency = null,Object? address = null,Object? city = null,Object? stateProvince = null,Object? country = null,Object? postalCode = freezed,Object? latitude = freezed,Object? longitude = freezed,Object? bedrooms = freezed,Object? bathrooms = freezed,Object? areaSqm = freezed,Object? floorNumber = freezed,Object? totalFloors = freezed,Object? isAvailable = null,Object? availableFrom = freezed,Object? status = null,Object? viewCount = freezed,Object? favoriteCount = freezed,Object? images = freezed,Object? videos = freezed,Object? amenities = freezed,Object? landlord = freezed,Object? createdAt = null,Object? updatedAt = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,title: null == title ? _self.title : title // ignore: cast_nullable_to_non_nullable
as String,description: null == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String,propertyType: null == propertyType ? _self.propertyType : propertyType // ignore: cast_nullable_to_non_nullable
as String,listingType: null == listingType ? _self.listingType : listingType // ignore: cast_nullable_to_non_nullable
as String,price: null == price ? _self.price : price // ignore: cast_nullable_to_non_nullable
as double,currency: null == currency ? _self.currency : currency // ignore: cast_nullable_to_non_nullable
as String,address: null == address ? _self.address : address // ignore: cast_nullable_to_non_nullable
as String,city: null == city ? _self.city : city // ignore: cast_nullable_to_non_nullable
as String,stateProvince: null == stateProvince ? _self.stateProvince : stateProvince // ignore: cast_nullable_to_non_nullable
as String,country: null == country ? _self.country : country // ignore: cast_nullable_to_non_nullable
as String,postalCode: freezed == postalCode ? _self.postalCode : postalCode // ignore: cast_nullable_to_non_nullable
as String?,latitude: freezed == latitude ? _self.latitude : latitude // ignore: cast_nullable_to_non_nullable
as double?,longitude: freezed == longitude ? _self.longitude : longitude // ignore: cast_nullable_to_non_nullable
as double?,bedrooms: freezed == bedrooms ? _self.bedrooms : bedrooms // ignore: cast_nullable_to_non_nullable
as int?,bathrooms: freezed == bathrooms ? _self.bathrooms : bathrooms // ignore: cast_nullable_to_non_nullable
as int?,areaSqm: freezed == areaSqm ? _self.areaSqm : areaSqm // ignore: cast_nullable_to_non_nullable
as double?,floorNumber: freezed == floorNumber ? _self.floorNumber : floorNumber // ignore: cast_nullable_to_non_nullable
as int?,totalFloors: freezed == totalFloors ? _self.totalFloors : totalFloors // ignore: cast_nullable_to_non_nullable
as int?,isAvailable: null == isAvailable ? _self.isAvailable : isAvailable // ignore: cast_nullable_to_non_nullable
as bool,availableFrom: freezed == availableFrom ? _self.availableFrom : availableFrom // ignore: cast_nullable_to_non_nullable
as String?,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,viewCount: freezed == viewCount ? _self.viewCount : viewCount // ignore: cast_nullable_to_non_nullable
as int?,favoriteCount: freezed == favoriteCount ? _self.favoriteCount : favoriteCount // ignore: cast_nullable_to_non_nullable
as int?,images: freezed == images ? _self.images : images // ignore: cast_nullable_to_non_nullable
as List<PropertyImageDto>?,videos: freezed == videos ? _self.videos : videos // ignore: cast_nullable_to_non_nullable
as List<PropertyVideoDto>?,amenities: freezed == amenities ? _self.amenities : amenities // ignore: cast_nullable_to_non_nullable
as List<AmenityDto>?,landlord: freezed == landlord ? _self.landlord : landlord // ignore: cast_nullable_to_non_nullable
as UserDto?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,updatedAt: null == updatedAt ? _self.updatedAt : updatedAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}
/// Create a copy of PropertyDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res>? get landlord {
    if (_self.landlord == null) {
    return null;
  }

  return $UserDtoCopyWith<$Res>(_self.landlord!, (value) {
    return _then(_self.copyWith(landlord: value));
  });
}
}


/// Adds pattern-matching-related methods to [PropertyDto].
extension PropertyDtoPatterns on PropertyDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _PropertyDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _PropertyDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _PropertyDto value)  $default,){
final _that = this;
switch (_that) {
case _PropertyDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _PropertyDto value)?  $default,){
final _that = this;
switch (_that) {
case _PropertyDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String title,  String description,  String propertyType,  String listingType,  double price,  String currency,  String address,  String city,  String stateProvince,  String country,  String? postalCode,  double? latitude,  double? longitude,  int? bedrooms,  int? bathrooms,  double? areaSqm,  int? floorNumber,  int? totalFloors,  bool isAvailable,  String? availableFrom,  String status,  int? viewCount,  int? favoriteCount,  List<PropertyImageDto>? images,  List<PropertyVideoDto>? videos,  List<AmenityDto>? amenities,  UserDto? landlord,  String createdAt,  String updatedAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _PropertyDto() when $default != null:
return $default(_that.id,_that.title,_that.description,_that.propertyType,_that.listingType,_that.price,_that.currency,_that.address,_that.city,_that.stateProvince,_that.country,_that.postalCode,_that.latitude,_that.longitude,_that.bedrooms,_that.bathrooms,_that.areaSqm,_that.floorNumber,_that.totalFloors,_that.isAvailable,_that.availableFrom,_that.status,_that.viewCount,_that.favoriteCount,_that.images,_that.videos,_that.amenities,_that.landlord,_that.createdAt,_that.updatedAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String title,  String description,  String propertyType,  String listingType,  double price,  String currency,  String address,  String city,  String stateProvince,  String country,  String? postalCode,  double? latitude,  double? longitude,  int? bedrooms,  int? bathrooms,  double? areaSqm,  int? floorNumber,  int? totalFloors,  bool isAvailable,  String? availableFrom,  String status,  int? viewCount,  int? favoriteCount,  List<PropertyImageDto>? images,  List<PropertyVideoDto>? videos,  List<AmenityDto>? amenities,  UserDto? landlord,  String createdAt,  String updatedAt)  $default,) {final _that = this;
switch (_that) {
case _PropertyDto():
return $default(_that.id,_that.title,_that.description,_that.propertyType,_that.listingType,_that.price,_that.currency,_that.address,_that.city,_that.stateProvince,_that.country,_that.postalCode,_that.latitude,_that.longitude,_that.bedrooms,_that.bathrooms,_that.areaSqm,_that.floorNumber,_that.totalFloors,_that.isAvailable,_that.availableFrom,_that.status,_that.viewCount,_that.favoriteCount,_that.images,_that.videos,_that.amenities,_that.landlord,_that.createdAt,_that.updatedAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String title,  String description,  String propertyType,  String listingType,  double price,  String currency,  String address,  String city,  String stateProvince,  String country,  String? postalCode,  double? latitude,  double? longitude,  int? bedrooms,  int? bathrooms,  double? areaSqm,  int? floorNumber,  int? totalFloors,  bool isAvailable,  String? availableFrom,  String status,  int? viewCount,  int? favoriteCount,  List<PropertyImageDto>? images,  List<PropertyVideoDto>? videos,  List<AmenityDto>? amenities,  UserDto? landlord,  String createdAt,  String updatedAt)?  $default,) {final _that = this;
switch (_that) {
case _PropertyDto() when $default != null:
return $default(_that.id,_that.title,_that.description,_that.propertyType,_that.listingType,_that.price,_that.currency,_that.address,_that.city,_that.stateProvince,_that.country,_that.postalCode,_that.latitude,_that.longitude,_that.bedrooms,_that.bathrooms,_that.areaSqm,_that.floorNumber,_that.totalFloors,_that.isAvailable,_that.availableFrom,_that.status,_that.viewCount,_that.favoriteCount,_that.images,_that.videos,_that.amenities,_that.landlord,_that.createdAt,_that.updatedAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _PropertyDto implements PropertyDto {
  const _PropertyDto({required this.id, required this.title, required this.description, required this.propertyType, required this.listingType, required this.price, required this.currency, required this.address, required this.city, required this.stateProvince, required this.country, this.postalCode, this.latitude, this.longitude, this.bedrooms, this.bathrooms, this.areaSqm, this.floorNumber, this.totalFloors, required this.isAvailable, this.availableFrom, required this.status, this.viewCount, this.favoriteCount, final  List<PropertyImageDto>? images, final  List<PropertyVideoDto>? videos, final  List<AmenityDto>? amenities, this.landlord, required this.createdAt, required this.updatedAt}): _images = images,_videos = videos,_amenities = amenities;
  factory _PropertyDto.fromJson(Map<String, dynamic> json) => _$PropertyDtoFromJson(json);

@override final  String id;
@override final  String title;
@override final  String description;
@override final  String propertyType;
@override final  String listingType;
@override final  double price;
@override final  String currency;
@override final  String address;
@override final  String city;
@override final  String stateProvince;
@override final  String country;
@override final  String? postalCode;
@override final  double? latitude;
@override final  double? longitude;
@override final  int? bedrooms;
@override final  int? bathrooms;
@override final  double? areaSqm;
@override final  int? floorNumber;
@override final  int? totalFloors;
@override final  bool isAvailable;
@override final  String? availableFrom;
@override final  String status;
@override final  int? viewCount;
@override final  int? favoriteCount;
 final  List<PropertyImageDto>? _images;
@override List<PropertyImageDto>? get images {
  final value = _images;
  if (value == null) return null;
  if (_images is EqualUnmodifiableListView) return _images;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(value);
}

 final  List<PropertyVideoDto>? _videos;
@override List<PropertyVideoDto>? get videos {
  final value = _videos;
  if (value == null) return null;
  if (_videos is EqualUnmodifiableListView) return _videos;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(value);
}

 final  List<AmenityDto>? _amenities;
@override List<AmenityDto>? get amenities {
  final value = _amenities;
  if (value == null) return null;
  if (_amenities is EqualUnmodifiableListView) return _amenities;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableListView(value);
}

@override final  UserDto? landlord;
@override final  String createdAt;
@override final  String updatedAt;

/// Create a copy of PropertyDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$PropertyDtoCopyWith<_PropertyDto> get copyWith => __$PropertyDtoCopyWithImpl<_PropertyDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PropertyDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _PropertyDto&&(identical(other.id, id) || other.id == id)&&(identical(other.title, title) || other.title == title)&&(identical(other.description, description) || other.description == description)&&(identical(other.propertyType, propertyType) || other.propertyType == propertyType)&&(identical(other.listingType, listingType) || other.listingType == listingType)&&(identical(other.price, price) || other.price == price)&&(identical(other.currency, currency) || other.currency == currency)&&(identical(other.address, address) || other.address == address)&&(identical(other.city, city) || other.city == city)&&(identical(other.stateProvince, stateProvince) || other.stateProvince == stateProvince)&&(identical(other.country, country) || other.country == country)&&(identical(other.postalCode, postalCode) || other.postalCode == postalCode)&&(identical(other.latitude, latitude) || other.latitude == latitude)&&(identical(other.longitude, longitude) || other.longitude == longitude)&&(identical(other.bedrooms, bedrooms) || other.bedrooms == bedrooms)&&(identical(other.bathrooms, bathrooms) || other.bathrooms == bathrooms)&&(identical(other.areaSqm, areaSqm) || other.areaSqm == areaSqm)&&(identical(other.floorNumber, floorNumber) || other.floorNumber == floorNumber)&&(identical(other.totalFloors, totalFloors) || other.totalFloors == totalFloors)&&(identical(other.isAvailable, isAvailable) || other.isAvailable == isAvailable)&&(identical(other.availableFrom, availableFrom) || other.availableFrom == availableFrom)&&(identical(other.status, status) || other.status == status)&&(identical(other.viewCount, viewCount) || other.viewCount == viewCount)&&(identical(other.favoriteCount, favoriteCount) || other.favoriteCount == favoriteCount)&&const DeepCollectionEquality().equals(other._images, _images)&&const DeepCollectionEquality().equals(other._videos, _videos)&&const DeepCollectionEquality().equals(other._amenities, _amenities)&&(identical(other.landlord, landlord) || other.landlord == landlord)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.updatedAt, updatedAt) || other.updatedAt == updatedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hashAll([runtimeType,id,title,description,propertyType,listingType,price,currency,address,city,stateProvince,country,postalCode,latitude,longitude,bedrooms,bathrooms,areaSqm,floorNumber,totalFloors,isAvailable,availableFrom,status,viewCount,favoriteCount,const DeepCollectionEquality().hash(_images),const DeepCollectionEquality().hash(_videos),const DeepCollectionEquality().hash(_amenities),landlord,createdAt,updatedAt]);

@override
String toString() {
  return 'PropertyDto(id: $id, title: $title, description: $description, propertyType: $propertyType, listingType: $listingType, price: $price, currency: $currency, address: $address, city: $city, stateProvince: $stateProvince, country: $country, postalCode: $postalCode, latitude: $latitude, longitude: $longitude, bedrooms: $bedrooms, bathrooms: $bathrooms, areaSqm: $areaSqm, floorNumber: $floorNumber, totalFloors: $totalFloors, isAvailable: $isAvailable, availableFrom: $availableFrom, status: $status, viewCount: $viewCount, favoriteCount: $favoriteCount, images: $images, videos: $videos, amenities: $amenities, landlord: $landlord, createdAt: $createdAt, updatedAt: $updatedAt)';
}


}

/// @nodoc
abstract mixin class _$PropertyDtoCopyWith<$Res> implements $PropertyDtoCopyWith<$Res> {
  factory _$PropertyDtoCopyWith(_PropertyDto value, $Res Function(_PropertyDto) _then) = __$PropertyDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String title, String description, String propertyType, String listingType, double price, String currency, String address, String city, String stateProvince, String country, String? postalCode, double? latitude, double? longitude, int? bedrooms, int? bathrooms, double? areaSqm, int? floorNumber, int? totalFloors, bool isAvailable, String? availableFrom, String status, int? viewCount, int? favoriteCount, List<PropertyImageDto>? images, List<PropertyVideoDto>? videos, List<AmenityDto>? amenities, UserDto? landlord, String createdAt, String updatedAt
});


@override $UserDtoCopyWith<$Res>? get landlord;

}
/// @nodoc
class __$PropertyDtoCopyWithImpl<$Res>
    implements _$PropertyDtoCopyWith<$Res> {
  __$PropertyDtoCopyWithImpl(this._self, this._then);

  final _PropertyDto _self;
  final $Res Function(_PropertyDto) _then;

/// Create a copy of PropertyDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? title = null,Object? description = null,Object? propertyType = null,Object? listingType = null,Object? price = null,Object? currency = null,Object? address = null,Object? city = null,Object? stateProvince = null,Object? country = null,Object? postalCode = freezed,Object? latitude = freezed,Object? longitude = freezed,Object? bedrooms = freezed,Object? bathrooms = freezed,Object? areaSqm = freezed,Object? floorNumber = freezed,Object? totalFloors = freezed,Object? isAvailable = null,Object? availableFrom = freezed,Object? status = null,Object? viewCount = freezed,Object? favoriteCount = freezed,Object? images = freezed,Object? videos = freezed,Object? amenities = freezed,Object? landlord = freezed,Object? createdAt = null,Object? updatedAt = null,}) {
  return _then(_PropertyDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,title: null == title ? _self.title : title // ignore: cast_nullable_to_non_nullable
as String,description: null == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String,propertyType: null == propertyType ? _self.propertyType : propertyType // ignore: cast_nullable_to_non_nullable
as String,listingType: null == listingType ? _self.listingType : listingType // ignore: cast_nullable_to_non_nullable
as String,price: null == price ? _self.price : price // ignore: cast_nullable_to_non_nullable
as double,currency: null == currency ? _self.currency : currency // ignore: cast_nullable_to_non_nullable
as String,address: null == address ? _self.address : address // ignore: cast_nullable_to_non_nullable
as String,city: null == city ? _self.city : city // ignore: cast_nullable_to_non_nullable
as String,stateProvince: null == stateProvince ? _self.stateProvince : stateProvince // ignore: cast_nullable_to_non_nullable
as String,country: null == country ? _self.country : country // ignore: cast_nullable_to_non_nullable
as String,postalCode: freezed == postalCode ? _self.postalCode : postalCode // ignore: cast_nullable_to_non_nullable
as String?,latitude: freezed == latitude ? _self.latitude : latitude // ignore: cast_nullable_to_non_nullable
as double?,longitude: freezed == longitude ? _self.longitude : longitude // ignore: cast_nullable_to_non_nullable
as double?,bedrooms: freezed == bedrooms ? _self.bedrooms : bedrooms // ignore: cast_nullable_to_non_nullable
as int?,bathrooms: freezed == bathrooms ? _self.bathrooms : bathrooms // ignore: cast_nullable_to_non_nullable
as int?,areaSqm: freezed == areaSqm ? _self.areaSqm : areaSqm // ignore: cast_nullable_to_non_nullable
as double?,floorNumber: freezed == floorNumber ? _self.floorNumber : floorNumber // ignore: cast_nullable_to_non_nullable
as int?,totalFloors: freezed == totalFloors ? _self.totalFloors : totalFloors // ignore: cast_nullable_to_non_nullable
as int?,isAvailable: null == isAvailable ? _self.isAvailable : isAvailable // ignore: cast_nullable_to_non_nullable
as bool,availableFrom: freezed == availableFrom ? _self.availableFrom : availableFrom // ignore: cast_nullable_to_non_nullable
as String?,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,viewCount: freezed == viewCount ? _self.viewCount : viewCount // ignore: cast_nullable_to_non_nullable
as int?,favoriteCount: freezed == favoriteCount ? _self.favoriteCount : favoriteCount // ignore: cast_nullable_to_non_nullable
as int?,images: freezed == images ? _self._images : images // ignore: cast_nullable_to_non_nullable
as List<PropertyImageDto>?,videos: freezed == videos ? _self._videos : videos // ignore: cast_nullable_to_non_nullable
as List<PropertyVideoDto>?,amenities: freezed == amenities ? _self._amenities : amenities // ignore: cast_nullable_to_non_nullable
as List<AmenityDto>?,landlord: freezed == landlord ? _self.landlord : landlord // ignore: cast_nullable_to_non_nullable
as UserDto?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,updatedAt: null == updatedAt ? _self.updatedAt : updatedAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

/// Create a copy of PropertyDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res>? get landlord {
    if (_self.landlord == null) {
    return null;
  }

  return $UserDtoCopyWith<$Res>(_self.landlord!, (value) {
    return _then(_self.copyWith(landlord: value));
  });
}
}

// dart format on
