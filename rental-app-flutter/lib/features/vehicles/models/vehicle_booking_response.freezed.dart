// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'vehicle_booking_response.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$VehicleBookingResponse {

 String get id; String get vehicleId; String get tenantId; String get startDate; String get endDate; double? get totalPrice; String get currency; String get status; double? get platformFee; String? get message; String get createdAt;
/// Create a copy of VehicleBookingResponse
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$VehicleBookingResponseCopyWith<VehicleBookingResponse> get copyWith => _$VehicleBookingResponseCopyWithImpl<VehicleBookingResponse>(this as VehicleBookingResponse, _$identity);

  /// Serializes this VehicleBookingResponse to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is VehicleBookingResponse&&(identical(other.id, id) || other.id == id)&&(identical(other.vehicleId, vehicleId) || other.vehicleId == vehicleId)&&(identical(other.tenantId, tenantId) || other.tenantId == tenantId)&&(identical(other.startDate, startDate) || other.startDate == startDate)&&(identical(other.endDate, endDate) || other.endDate == endDate)&&(identical(other.totalPrice, totalPrice) || other.totalPrice == totalPrice)&&(identical(other.currency, currency) || other.currency == currency)&&(identical(other.status, status) || other.status == status)&&(identical(other.platformFee, platformFee) || other.platformFee == platformFee)&&(identical(other.message, message) || other.message == message)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,vehicleId,tenantId,startDate,endDate,totalPrice,currency,status,platformFee,message,createdAt);

@override
String toString() {
  return 'VehicleBookingResponse(id: $id, vehicleId: $vehicleId, tenantId: $tenantId, startDate: $startDate, endDate: $endDate, totalPrice: $totalPrice, currency: $currency, status: $status, platformFee: $platformFee, message: $message, createdAt: $createdAt)';
}


}

/// @nodoc
abstract mixin class $VehicleBookingResponseCopyWith<$Res>  {
  factory $VehicleBookingResponseCopyWith(VehicleBookingResponse value, $Res Function(VehicleBookingResponse) _then) = _$VehicleBookingResponseCopyWithImpl;
@useResult
$Res call({
 String id, String vehicleId, String tenantId, String startDate, String endDate, double? totalPrice, String currency, String status, double? platformFee, String? message, String createdAt
});




}
/// @nodoc
class _$VehicleBookingResponseCopyWithImpl<$Res>
    implements $VehicleBookingResponseCopyWith<$Res> {
  _$VehicleBookingResponseCopyWithImpl(this._self, this._then);

  final VehicleBookingResponse _self;
  final $Res Function(VehicleBookingResponse) _then;

/// Create a copy of VehicleBookingResponse
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? vehicleId = null,Object? tenantId = null,Object? startDate = null,Object? endDate = null,Object? totalPrice = freezed,Object? currency = null,Object? status = null,Object? platformFee = freezed,Object? message = freezed,Object? createdAt = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,vehicleId: null == vehicleId ? _self.vehicleId : vehicleId // ignore: cast_nullable_to_non_nullable
as String,tenantId: null == tenantId ? _self.tenantId : tenantId // ignore: cast_nullable_to_non_nullable
as String,startDate: null == startDate ? _self.startDate : startDate // ignore: cast_nullable_to_non_nullable
as String,endDate: null == endDate ? _self.endDate : endDate // ignore: cast_nullable_to_non_nullable
as String,totalPrice: freezed == totalPrice ? _self.totalPrice : totalPrice // ignore: cast_nullable_to_non_nullable
as double?,currency: null == currency ? _self.currency : currency // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,platformFee: freezed == platformFee ? _self.platformFee : platformFee // ignore: cast_nullable_to_non_nullable
as double?,message: freezed == message ? _self.message : message // ignore: cast_nullable_to_non_nullable
as String?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [VehicleBookingResponse].
extension VehicleBookingResponsePatterns on VehicleBookingResponse {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _VehicleBookingResponse value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _VehicleBookingResponse() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _VehicleBookingResponse value)  $default,){
final _that = this;
switch (_that) {
case _VehicleBookingResponse():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _VehicleBookingResponse value)?  $default,){
final _that = this;
switch (_that) {
case _VehicleBookingResponse() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String vehicleId,  String tenantId,  String startDate,  String endDate,  double? totalPrice,  String currency,  String status,  double? platformFee,  String? message,  String createdAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _VehicleBookingResponse() when $default != null:
return $default(_that.id,_that.vehicleId,_that.tenantId,_that.startDate,_that.endDate,_that.totalPrice,_that.currency,_that.status,_that.platformFee,_that.message,_that.createdAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String vehicleId,  String tenantId,  String startDate,  String endDate,  double? totalPrice,  String currency,  String status,  double? platformFee,  String? message,  String createdAt)  $default,) {final _that = this;
switch (_that) {
case _VehicleBookingResponse():
return $default(_that.id,_that.vehicleId,_that.tenantId,_that.startDate,_that.endDate,_that.totalPrice,_that.currency,_that.status,_that.platformFee,_that.message,_that.createdAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String vehicleId,  String tenantId,  String startDate,  String endDate,  double? totalPrice,  String currency,  String status,  double? platformFee,  String? message,  String createdAt)?  $default,) {final _that = this;
switch (_that) {
case _VehicleBookingResponse() when $default != null:
return $default(_that.id,_that.vehicleId,_that.tenantId,_that.startDate,_that.endDate,_that.totalPrice,_that.currency,_that.status,_that.platformFee,_that.message,_that.createdAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _VehicleBookingResponse implements VehicleBookingResponse {
  const _VehicleBookingResponse({required this.id, required this.vehicleId, required this.tenantId, required this.startDate, required this.endDate, this.totalPrice, required this.currency, required this.status, this.platformFee, this.message, required this.createdAt});
  factory _VehicleBookingResponse.fromJson(Map<String, dynamic> json) => _$VehicleBookingResponseFromJson(json);

@override final  String id;
@override final  String vehicleId;
@override final  String tenantId;
@override final  String startDate;
@override final  String endDate;
@override final  double? totalPrice;
@override final  String currency;
@override final  String status;
@override final  double? platformFee;
@override final  String? message;
@override final  String createdAt;

/// Create a copy of VehicleBookingResponse
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$VehicleBookingResponseCopyWith<_VehicleBookingResponse> get copyWith => __$VehicleBookingResponseCopyWithImpl<_VehicleBookingResponse>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$VehicleBookingResponseToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _VehicleBookingResponse&&(identical(other.id, id) || other.id == id)&&(identical(other.vehicleId, vehicleId) || other.vehicleId == vehicleId)&&(identical(other.tenantId, tenantId) || other.tenantId == tenantId)&&(identical(other.startDate, startDate) || other.startDate == startDate)&&(identical(other.endDate, endDate) || other.endDate == endDate)&&(identical(other.totalPrice, totalPrice) || other.totalPrice == totalPrice)&&(identical(other.currency, currency) || other.currency == currency)&&(identical(other.status, status) || other.status == status)&&(identical(other.platformFee, platformFee) || other.platformFee == platformFee)&&(identical(other.message, message) || other.message == message)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,vehicleId,tenantId,startDate,endDate,totalPrice,currency,status,platformFee,message,createdAt);

@override
String toString() {
  return 'VehicleBookingResponse(id: $id, vehicleId: $vehicleId, tenantId: $tenantId, startDate: $startDate, endDate: $endDate, totalPrice: $totalPrice, currency: $currency, status: $status, platformFee: $platformFee, message: $message, createdAt: $createdAt)';
}


}

/// @nodoc
abstract mixin class _$VehicleBookingResponseCopyWith<$Res> implements $VehicleBookingResponseCopyWith<$Res> {
  factory _$VehicleBookingResponseCopyWith(_VehicleBookingResponse value, $Res Function(_VehicleBookingResponse) _then) = __$VehicleBookingResponseCopyWithImpl;
@override @useResult
$Res call({
 String id, String vehicleId, String tenantId, String startDate, String endDate, double? totalPrice, String currency, String status, double? platformFee, String? message, String createdAt
});




}
/// @nodoc
class __$VehicleBookingResponseCopyWithImpl<$Res>
    implements _$VehicleBookingResponseCopyWith<$Res> {
  __$VehicleBookingResponseCopyWithImpl(this._self, this._then);

  final _VehicleBookingResponse _self;
  final $Res Function(_VehicleBookingResponse) _then;

/// Create a copy of VehicleBookingResponse
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? vehicleId = null,Object? tenantId = null,Object? startDate = null,Object? endDate = null,Object? totalPrice = freezed,Object? currency = null,Object? status = null,Object? platformFee = freezed,Object? message = freezed,Object? createdAt = null,}) {
  return _then(_VehicleBookingResponse(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,vehicleId: null == vehicleId ? _self.vehicleId : vehicleId // ignore: cast_nullable_to_non_nullable
as String,tenantId: null == tenantId ? _self.tenantId : tenantId // ignore: cast_nullable_to_non_nullable
as String,startDate: null == startDate ? _self.startDate : startDate // ignore: cast_nullable_to_non_nullable
as String,endDate: null == endDate ? _self.endDate : endDate // ignore: cast_nullable_to_non_nullable
as String,totalPrice: freezed == totalPrice ? _self.totalPrice : totalPrice // ignore: cast_nullable_to_non_nullable
as double?,currency: null == currency ? _self.currency : currency // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,platformFee: freezed == platformFee ? _self.platformFee : platformFee // ignore: cast_nullable_to_non_nullable
as double?,message: freezed == message ? _self.message : message // ignore: cast_nullable_to_non_nullable
as String?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
