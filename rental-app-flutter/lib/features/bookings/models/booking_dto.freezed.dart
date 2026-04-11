// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'booking_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$BookingDto {

 String get id; PropertyDto? get property; UserDto? get tenant; String get bookingType; String? get requestedDate; String? get startDate; String? get endDate; String get status; String? get message; int? get numberOfOccupants; double? get totalPrice; double? get platformFee; String? get stripePaymentIntentId; String? get paymentConfirmedAt; String? get escrowReleasedAt; String? get landlordResponse; String? get respondedAt; String get createdAt;
/// Create a copy of BookingDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$BookingDtoCopyWith<BookingDto> get copyWith => _$BookingDtoCopyWithImpl<BookingDto>(this as BookingDto, _$identity);

  /// Serializes this BookingDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is BookingDto&&(identical(other.id, id) || other.id == id)&&(identical(other.property, property) || other.property == property)&&(identical(other.tenant, tenant) || other.tenant == tenant)&&(identical(other.bookingType, bookingType) || other.bookingType == bookingType)&&(identical(other.requestedDate, requestedDate) || other.requestedDate == requestedDate)&&(identical(other.startDate, startDate) || other.startDate == startDate)&&(identical(other.endDate, endDate) || other.endDate == endDate)&&(identical(other.status, status) || other.status == status)&&(identical(other.message, message) || other.message == message)&&(identical(other.numberOfOccupants, numberOfOccupants) || other.numberOfOccupants == numberOfOccupants)&&(identical(other.totalPrice, totalPrice) || other.totalPrice == totalPrice)&&(identical(other.platformFee, platformFee) || other.platformFee == platformFee)&&(identical(other.stripePaymentIntentId, stripePaymentIntentId) || other.stripePaymentIntentId == stripePaymentIntentId)&&(identical(other.paymentConfirmedAt, paymentConfirmedAt) || other.paymentConfirmedAt == paymentConfirmedAt)&&(identical(other.escrowReleasedAt, escrowReleasedAt) || other.escrowReleasedAt == escrowReleasedAt)&&(identical(other.landlordResponse, landlordResponse) || other.landlordResponse == landlordResponse)&&(identical(other.respondedAt, respondedAt) || other.respondedAt == respondedAt)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,property,tenant,bookingType,requestedDate,startDate,endDate,status,message,numberOfOccupants,totalPrice,platformFee,stripePaymentIntentId,paymentConfirmedAt,escrowReleasedAt,landlordResponse,respondedAt,createdAt);

@override
String toString() {
  return 'BookingDto(id: $id, property: $property, tenant: $tenant, bookingType: $bookingType, requestedDate: $requestedDate, startDate: $startDate, endDate: $endDate, status: $status, message: $message, numberOfOccupants: $numberOfOccupants, totalPrice: $totalPrice, platformFee: $platformFee, stripePaymentIntentId: $stripePaymentIntentId, paymentConfirmedAt: $paymentConfirmedAt, escrowReleasedAt: $escrowReleasedAt, landlordResponse: $landlordResponse, respondedAt: $respondedAt, createdAt: $createdAt)';
}


}

/// @nodoc
abstract mixin class $BookingDtoCopyWith<$Res>  {
  factory $BookingDtoCopyWith(BookingDto value, $Res Function(BookingDto) _then) = _$BookingDtoCopyWithImpl;
@useResult
$Res call({
 String id, PropertyDto? property, UserDto? tenant, String bookingType, String? requestedDate, String? startDate, String? endDate, String status, String? message, int? numberOfOccupants, double? totalPrice, double? platformFee, String? stripePaymentIntentId, String? paymentConfirmedAt, String? escrowReleasedAt, String? landlordResponse, String? respondedAt, String createdAt
});


$PropertyDtoCopyWith<$Res>? get property;$UserDtoCopyWith<$Res>? get tenant;

}
/// @nodoc
class _$BookingDtoCopyWithImpl<$Res>
    implements $BookingDtoCopyWith<$Res> {
  _$BookingDtoCopyWithImpl(this._self, this._then);

  final BookingDto _self;
  final $Res Function(BookingDto) _then;

/// Create a copy of BookingDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? property = freezed,Object? tenant = freezed,Object? bookingType = null,Object? requestedDate = freezed,Object? startDate = freezed,Object? endDate = freezed,Object? status = null,Object? message = freezed,Object? numberOfOccupants = freezed,Object? totalPrice = freezed,Object? platformFee = freezed,Object? stripePaymentIntentId = freezed,Object? paymentConfirmedAt = freezed,Object? escrowReleasedAt = freezed,Object? landlordResponse = freezed,Object? respondedAt = freezed,Object? createdAt = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,property: freezed == property ? _self.property : property // ignore: cast_nullable_to_non_nullable
as PropertyDto?,tenant: freezed == tenant ? _self.tenant : tenant // ignore: cast_nullable_to_non_nullable
as UserDto?,bookingType: null == bookingType ? _self.bookingType : bookingType // ignore: cast_nullable_to_non_nullable
as String,requestedDate: freezed == requestedDate ? _self.requestedDate : requestedDate // ignore: cast_nullable_to_non_nullable
as String?,startDate: freezed == startDate ? _self.startDate : startDate // ignore: cast_nullable_to_non_nullable
as String?,endDate: freezed == endDate ? _self.endDate : endDate // ignore: cast_nullable_to_non_nullable
as String?,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,message: freezed == message ? _self.message : message // ignore: cast_nullable_to_non_nullable
as String?,numberOfOccupants: freezed == numberOfOccupants ? _self.numberOfOccupants : numberOfOccupants // ignore: cast_nullable_to_non_nullable
as int?,totalPrice: freezed == totalPrice ? _self.totalPrice : totalPrice // ignore: cast_nullable_to_non_nullable
as double?,platformFee: freezed == platformFee ? _self.platformFee : platformFee // ignore: cast_nullable_to_non_nullable
as double?,stripePaymentIntentId: freezed == stripePaymentIntentId ? _self.stripePaymentIntentId : stripePaymentIntentId // ignore: cast_nullable_to_non_nullable
as String?,paymentConfirmedAt: freezed == paymentConfirmedAt ? _self.paymentConfirmedAt : paymentConfirmedAt // ignore: cast_nullable_to_non_nullable
as String?,escrowReleasedAt: freezed == escrowReleasedAt ? _self.escrowReleasedAt : escrowReleasedAt // ignore: cast_nullable_to_non_nullable
as String?,landlordResponse: freezed == landlordResponse ? _self.landlordResponse : landlordResponse // ignore: cast_nullable_to_non_nullable
as String?,respondedAt: freezed == respondedAt ? _self.respondedAt : respondedAt // ignore: cast_nullable_to_non_nullable
as String?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}
/// Create a copy of BookingDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PropertyDtoCopyWith<$Res>? get property {
    if (_self.property == null) {
    return null;
  }

  return $PropertyDtoCopyWith<$Res>(_self.property!, (value) {
    return _then(_self.copyWith(property: value));
  });
}/// Create a copy of BookingDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res>? get tenant {
    if (_self.tenant == null) {
    return null;
  }

  return $UserDtoCopyWith<$Res>(_self.tenant!, (value) {
    return _then(_self.copyWith(tenant: value));
  });
}
}


/// Adds pattern-matching-related methods to [BookingDto].
extension BookingDtoPatterns on BookingDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _BookingDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _BookingDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _BookingDto value)  $default,){
final _that = this;
switch (_that) {
case _BookingDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _BookingDto value)?  $default,){
final _that = this;
switch (_that) {
case _BookingDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  PropertyDto? property,  UserDto? tenant,  String bookingType,  String? requestedDate,  String? startDate,  String? endDate,  String status,  String? message,  int? numberOfOccupants,  double? totalPrice,  double? platformFee,  String? stripePaymentIntentId,  String? paymentConfirmedAt,  String? escrowReleasedAt,  String? landlordResponse,  String? respondedAt,  String createdAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _BookingDto() when $default != null:
return $default(_that.id,_that.property,_that.tenant,_that.bookingType,_that.requestedDate,_that.startDate,_that.endDate,_that.status,_that.message,_that.numberOfOccupants,_that.totalPrice,_that.platformFee,_that.stripePaymentIntentId,_that.paymentConfirmedAt,_that.escrowReleasedAt,_that.landlordResponse,_that.respondedAt,_that.createdAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  PropertyDto? property,  UserDto? tenant,  String bookingType,  String? requestedDate,  String? startDate,  String? endDate,  String status,  String? message,  int? numberOfOccupants,  double? totalPrice,  double? platformFee,  String? stripePaymentIntentId,  String? paymentConfirmedAt,  String? escrowReleasedAt,  String? landlordResponse,  String? respondedAt,  String createdAt)  $default,) {final _that = this;
switch (_that) {
case _BookingDto():
return $default(_that.id,_that.property,_that.tenant,_that.bookingType,_that.requestedDate,_that.startDate,_that.endDate,_that.status,_that.message,_that.numberOfOccupants,_that.totalPrice,_that.platformFee,_that.stripePaymentIntentId,_that.paymentConfirmedAt,_that.escrowReleasedAt,_that.landlordResponse,_that.respondedAt,_that.createdAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  PropertyDto? property,  UserDto? tenant,  String bookingType,  String? requestedDate,  String? startDate,  String? endDate,  String status,  String? message,  int? numberOfOccupants,  double? totalPrice,  double? platformFee,  String? stripePaymentIntentId,  String? paymentConfirmedAt,  String? escrowReleasedAt,  String? landlordResponse,  String? respondedAt,  String createdAt)?  $default,) {final _that = this;
switch (_that) {
case _BookingDto() when $default != null:
return $default(_that.id,_that.property,_that.tenant,_that.bookingType,_that.requestedDate,_that.startDate,_that.endDate,_that.status,_that.message,_that.numberOfOccupants,_that.totalPrice,_that.platformFee,_that.stripePaymentIntentId,_that.paymentConfirmedAt,_that.escrowReleasedAt,_that.landlordResponse,_that.respondedAt,_that.createdAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _BookingDto implements BookingDto {
  const _BookingDto({required this.id, this.property, this.tenant, required this.bookingType, this.requestedDate, this.startDate, this.endDate, required this.status, this.message, this.numberOfOccupants, this.totalPrice, this.platformFee, this.stripePaymentIntentId, this.paymentConfirmedAt, this.escrowReleasedAt, this.landlordResponse, this.respondedAt, required this.createdAt});
  factory _BookingDto.fromJson(Map<String, dynamic> json) => _$BookingDtoFromJson(json);

@override final  String id;
@override final  PropertyDto? property;
@override final  UserDto? tenant;
@override final  String bookingType;
@override final  String? requestedDate;
@override final  String? startDate;
@override final  String? endDate;
@override final  String status;
@override final  String? message;
@override final  int? numberOfOccupants;
@override final  double? totalPrice;
@override final  double? platformFee;
@override final  String? stripePaymentIntentId;
@override final  String? paymentConfirmedAt;
@override final  String? escrowReleasedAt;
@override final  String? landlordResponse;
@override final  String? respondedAt;
@override final  String createdAt;

/// Create a copy of BookingDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$BookingDtoCopyWith<_BookingDto> get copyWith => __$BookingDtoCopyWithImpl<_BookingDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$BookingDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _BookingDto&&(identical(other.id, id) || other.id == id)&&(identical(other.property, property) || other.property == property)&&(identical(other.tenant, tenant) || other.tenant == tenant)&&(identical(other.bookingType, bookingType) || other.bookingType == bookingType)&&(identical(other.requestedDate, requestedDate) || other.requestedDate == requestedDate)&&(identical(other.startDate, startDate) || other.startDate == startDate)&&(identical(other.endDate, endDate) || other.endDate == endDate)&&(identical(other.status, status) || other.status == status)&&(identical(other.message, message) || other.message == message)&&(identical(other.numberOfOccupants, numberOfOccupants) || other.numberOfOccupants == numberOfOccupants)&&(identical(other.totalPrice, totalPrice) || other.totalPrice == totalPrice)&&(identical(other.platformFee, platformFee) || other.platformFee == platformFee)&&(identical(other.stripePaymentIntentId, stripePaymentIntentId) || other.stripePaymentIntentId == stripePaymentIntentId)&&(identical(other.paymentConfirmedAt, paymentConfirmedAt) || other.paymentConfirmedAt == paymentConfirmedAt)&&(identical(other.escrowReleasedAt, escrowReleasedAt) || other.escrowReleasedAt == escrowReleasedAt)&&(identical(other.landlordResponse, landlordResponse) || other.landlordResponse == landlordResponse)&&(identical(other.respondedAt, respondedAt) || other.respondedAt == respondedAt)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,property,tenant,bookingType,requestedDate,startDate,endDate,status,message,numberOfOccupants,totalPrice,platformFee,stripePaymentIntentId,paymentConfirmedAt,escrowReleasedAt,landlordResponse,respondedAt,createdAt);

@override
String toString() {
  return 'BookingDto(id: $id, property: $property, tenant: $tenant, bookingType: $bookingType, requestedDate: $requestedDate, startDate: $startDate, endDate: $endDate, status: $status, message: $message, numberOfOccupants: $numberOfOccupants, totalPrice: $totalPrice, platformFee: $platformFee, stripePaymentIntentId: $stripePaymentIntentId, paymentConfirmedAt: $paymentConfirmedAt, escrowReleasedAt: $escrowReleasedAt, landlordResponse: $landlordResponse, respondedAt: $respondedAt, createdAt: $createdAt)';
}


}

/// @nodoc
abstract mixin class _$BookingDtoCopyWith<$Res> implements $BookingDtoCopyWith<$Res> {
  factory _$BookingDtoCopyWith(_BookingDto value, $Res Function(_BookingDto) _then) = __$BookingDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, PropertyDto? property, UserDto? tenant, String bookingType, String? requestedDate, String? startDate, String? endDate, String status, String? message, int? numberOfOccupants, double? totalPrice, double? platformFee, String? stripePaymentIntentId, String? paymentConfirmedAt, String? escrowReleasedAt, String? landlordResponse, String? respondedAt, String createdAt
});


@override $PropertyDtoCopyWith<$Res>? get property;@override $UserDtoCopyWith<$Res>? get tenant;

}
/// @nodoc
class __$BookingDtoCopyWithImpl<$Res>
    implements _$BookingDtoCopyWith<$Res> {
  __$BookingDtoCopyWithImpl(this._self, this._then);

  final _BookingDto _self;
  final $Res Function(_BookingDto) _then;

/// Create a copy of BookingDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? property = freezed,Object? tenant = freezed,Object? bookingType = null,Object? requestedDate = freezed,Object? startDate = freezed,Object? endDate = freezed,Object? status = null,Object? message = freezed,Object? numberOfOccupants = freezed,Object? totalPrice = freezed,Object? platformFee = freezed,Object? stripePaymentIntentId = freezed,Object? paymentConfirmedAt = freezed,Object? escrowReleasedAt = freezed,Object? landlordResponse = freezed,Object? respondedAt = freezed,Object? createdAt = null,}) {
  return _then(_BookingDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,property: freezed == property ? _self.property : property // ignore: cast_nullable_to_non_nullable
as PropertyDto?,tenant: freezed == tenant ? _self.tenant : tenant // ignore: cast_nullable_to_non_nullable
as UserDto?,bookingType: null == bookingType ? _self.bookingType : bookingType // ignore: cast_nullable_to_non_nullable
as String,requestedDate: freezed == requestedDate ? _self.requestedDate : requestedDate // ignore: cast_nullable_to_non_nullable
as String?,startDate: freezed == startDate ? _self.startDate : startDate // ignore: cast_nullable_to_non_nullable
as String?,endDate: freezed == endDate ? _self.endDate : endDate // ignore: cast_nullable_to_non_nullable
as String?,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,message: freezed == message ? _self.message : message // ignore: cast_nullable_to_non_nullable
as String?,numberOfOccupants: freezed == numberOfOccupants ? _self.numberOfOccupants : numberOfOccupants // ignore: cast_nullable_to_non_nullable
as int?,totalPrice: freezed == totalPrice ? _self.totalPrice : totalPrice // ignore: cast_nullable_to_non_nullable
as double?,platformFee: freezed == platformFee ? _self.platformFee : platformFee // ignore: cast_nullable_to_non_nullable
as double?,stripePaymentIntentId: freezed == stripePaymentIntentId ? _self.stripePaymentIntentId : stripePaymentIntentId // ignore: cast_nullable_to_non_nullable
as String?,paymentConfirmedAt: freezed == paymentConfirmedAt ? _self.paymentConfirmedAt : paymentConfirmedAt // ignore: cast_nullable_to_non_nullable
as String?,escrowReleasedAt: freezed == escrowReleasedAt ? _self.escrowReleasedAt : escrowReleasedAt // ignore: cast_nullable_to_non_nullable
as String?,landlordResponse: freezed == landlordResponse ? _self.landlordResponse : landlordResponse // ignore: cast_nullable_to_non_nullable
as String?,respondedAt: freezed == respondedAt ? _self.respondedAt : respondedAt // ignore: cast_nullable_to_non_nullable
as String?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

/// Create a copy of BookingDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$PropertyDtoCopyWith<$Res>? get property {
    if (_self.property == null) {
    return null;
  }

  return $PropertyDtoCopyWith<$Res>(_self.property!, (value) {
    return _then(_self.copyWith(property: value));
  });
}/// Create a copy of BookingDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res>? get tenant {
    if (_self.tenant == null) {
    return null;
  }

  return $UserDtoCopyWith<$Res>(_self.tenant!, (value) {
    return _then(_self.copyWith(tenant: value));
  });
}
}

// dart format on
