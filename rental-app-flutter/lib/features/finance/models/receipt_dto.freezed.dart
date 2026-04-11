// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'receipt_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ReceiptDto {

 String get id; String? get bookingId; String get userId; String get receiptNumber; double get amount; String get currency; String get status; String? get receiptUrl; String get issuedAt; String get createdAt;
/// Create a copy of ReceiptDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ReceiptDtoCopyWith<ReceiptDto> get copyWith => _$ReceiptDtoCopyWithImpl<ReceiptDto>(this as ReceiptDto, _$identity);

  /// Serializes this ReceiptDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ReceiptDto&&(identical(other.id, id) || other.id == id)&&(identical(other.bookingId, bookingId) || other.bookingId == bookingId)&&(identical(other.userId, userId) || other.userId == userId)&&(identical(other.receiptNumber, receiptNumber) || other.receiptNumber == receiptNumber)&&(identical(other.amount, amount) || other.amount == amount)&&(identical(other.currency, currency) || other.currency == currency)&&(identical(other.status, status) || other.status == status)&&(identical(other.receiptUrl, receiptUrl) || other.receiptUrl == receiptUrl)&&(identical(other.issuedAt, issuedAt) || other.issuedAt == issuedAt)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,bookingId,userId,receiptNumber,amount,currency,status,receiptUrl,issuedAt,createdAt);

@override
String toString() {
  return 'ReceiptDto(id: $id, bookingId: $bookingId, userId: $userId, receiptNumber: $receiptNumber, amount: $amount, currency: $currency, status: $status, receiptUrl: $receiptUrl, issuedAt: $issuedAt, createdAt: $createdAt)';
}


}

/// @nodoc
abstract mixin class $ReceiptDtoCopyWith<$Res>  {
  factory $ReceiptDtoCopyWith(ReceiptDto value, $Res Function(ReceiptDto) _then) = _$ReceiptDtoCopyWithImpl;
@useResult
$Res call({
 String id, String? bookingId, String userId, String receiptNumber, double amount, String currency, String status, String? receiptUrl, String issuedAt, String createdAt
});




}
/// @nodoc
class _$ReceiptDtoCopyWithImpl<$Res>
    implements $ReceiptDtoCopyWith<$Res> {
  _$ReceiptDtoCopyWithImpl(this._self, this._then);

  final ReceiptDto _self;
  final $Res Function(ReceiptDto) _then;

/// Create a copy of ReceiptDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? bookingId = freezed,Object? userId = null,Object? receiptNumber = null,Object? amount = null,Object? currency = null,Object? status = null,Object? receiptUrl = freezed,Object? issuedAt = null,Object? createdAt = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,bookingId: freezed == bookingId ? _self.bookingId : bookingId // ignore: cast_nullable_to_non_nullable
as String?,userId: null == userId ? _self.userId : userId // ignore: cast_nullable_to_non_nullable
as String,receiptNumber: null == receiptNumber ? _self.receiptNumber : receiptNumber // ignore: cast_nullable_to_non_nullable
as String,amount: null == amount ? _self.amount : amount // ignore: cast_nullable_to_non_nullable
as double,currency: null == currency ? _self.currency : currency // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,receiptUrl: freezed == receiptUrl ? _self.receiptUrl : receiptUrl // ignore: cast_nullable_to_non_nullable
as String?,issuedAt: null == issuedAt ? _self.issuedAt : issuedAt // ignore: cast_nullable_to_non_nullable
as String,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [ReceiptDto].
extension ReceiptDtoPatterns on ReceiptDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ReceiptDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ReceiptDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ReceiptDto value)  $default,){
final _that = this;
switch (_that) {
case _ReceiptDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ReceiptDto value)?  $default,){
final _that = this;
switch (_that) {
case _ReceiptDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String? bookingId,  String userId,  String receiptNumber,  double amount,  String currency,  String status,  String? receiptUrl,  String issuedAt,  String createdAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ReceiptDto() when $default != null:
return $default(_that.id,_that.bookingId,_that.userId,_that.receiptNumber,_that.amount,_that.currency,_that.status,_that.receiptUrl,_that.issuedAt,_that.createdAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String? bookingId,  String userId,  String receiptNumber,  double amount,  String currency,  String status,  String? receiptUrl,  String issuedAt,  String createdAt)  $default,) {final _that = this;
switch (_that) {
case _ReceiptDto():
return $default(_that.id,_that.bookingId,_that.userId,_that.receiptNumber,_that.amount,_that.currency,_that.status,_that.receiptUrl,_that.issuedAt,_that.createdAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String? bookingId,  String userId,  String receiptNumber,  double amount,  String currency,  String status,  String? receiptUrl,  String issuedAt,  String createdAt)?  $default,) {final _that = this;
switch (_that) {
case _ReceiptDto() when $default != null:
return $default(_that.id,_that.bookingId,_that.userId,_that.receiptNumber,_that.amount,_that.currency,_that.status,_that.receiptUrl,_that.issuedAt,_that.createdAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ReceiptDto implements ReceiptDto {
  const _ReceiptDto({required this.id, this.bookingId, required this.userId, required this.receiptNumber, required this.amount, required this.currency, required this.status, this.receiptUrl, required this.issuedAt, required this.createdAt});
  factory _ReceiptDto.fromJson(Map<String, dynamic> json) => _$ReceiptDtoFromJson(json);

@override final  String id;
@override final  String? bookingId;
@override final  String userId;
@override final  String receiptNumber;
@override final  double amount;
@override final  String currency;
@override final  String status;
@override final  String? receiptUrl;
@override final  String issuedAt;
@override final  String createdAt;

/// Create a copy of ReceiptDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ReceiptDtoCopyWith<_ReceiptDto> get copyWith => __$ReceiptDtoCopyWithImpl<_ReceiptDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ReceiptDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _ReceiptDto&&(identical(other.id, id) || other.id == id)&&(identical(other.bookingId, bookingId) || other.bookingId == bookingId)&&(identical(other.userId, userId) || other.userId == userId)&&(identical(other.receiptNumber, receiptNumber) || other.receiptNumber == receiptNumber)&&(identical(other.amount, amount) || other.amount == amount)&&(identical(other.currency, currency) || other.currency == currency)&&(identical(other.status, status) || other.status == status)&&(identical(other.receiptUrl, receiptUrl) || other.receiptUrl == receiptUrl)&&(identical(other.issuedAt, issuedAt) || other.issuedAt == issuedAt)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,bookingId,userId,receiptNumber,amount,currency,status,receiptUrl,issuedAt,createdAt);

@override
String toString() {
  return 'ReceiptDto(id: $id, bookingId: $bookingId, userId: $userId, receiptNumber: $receiptNumber, amount: $amount, currency: $currency, status: $status, receiptUrl: $receiptUrl, issuedAt: $issuedAt, createdAt: $createdAt)';
}


}

/// @nodoc
abstract mixin class _$ReceiptDtoCopyWith<$Res> implements $ReceiptDtoCopyWith<$Res> {
  factory _$ReceiptDtoCopyWith(_ReceiptDto value, $Res Function(_ReceiptDto) _then) = __$ReceiptDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String? bookingId, String userId, String receiptNumber, double amount, String currency, String status, String? receiptUrl, String issuedAt, String createdAt
});




}
/// @nodoc
class __$ReceiptDtoCopyWithImpl<$Res>
    implements _$ReceiptDtoCopyWith<$Res> {
  __$ReceiptDtoCopyWithImpl(this._self, this._then);

  final _ReceiptDto _self;
  final $Res Function(_ReceiptDto) _then;

/// Create a copy of ReceiptDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? bookingId = freezed,Object? userId = null,Object? receiptNumber = null,Object? amount = null,Object? currency = null,Object? status = null,Object? receiptUrl = freezed,Object? issuedAt = null,Object? createdAt = null,}) {
  return _then(_ReceiptDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,bookingId: freezed == bookingId ? _self.bookingId : bookingId // ignore: cast_nullable_to_non_nullable
as String?,userId: null == userId ? _self.userId : userId // ignore: cast_nullable_to_non_nullable
as String,receiptNumber: null == receiptNumber ? _self.receiptNumber : receiptNumber // ignore: cast_nullable_to_non_nullable
as String,amount: null == amount ? _self.amount : amount // ignore: cast_nullable_to_non_nullable
as double,currency: null == currency ? _self.currency : currency // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,receiptUrl: freezed == receiptUrl ? _self.receiptUrl : receiptUrl // ignore: cast_nullable_to_non_nullable
as String?,issuedAt: null == issuedAt ? _self.issuedAt : issuedAt // ignore: cast_nullable_to_non_nullable
as String,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
