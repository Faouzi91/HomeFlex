// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'booking_modification_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$BookingModificationRequest {

 String get startDate; String get endDate; String? get reason;
/// Create a copy of BookingModificationRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$BookingModificationRequestCopyWith<BookingModificationRequest> get copyWith => _$BookingModificationRequestCopyWithImpl<BookingModificationRequest>(this as BookingModificationRequest, _$identity);

  /// Serializes this BookingModificationRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is BookingModificationRequest&&(identical(other.startDate, startDate) || other.startDate == startDate)&&(identical(other.endDate, endDate) || other.endDate == endDate)&&(identical(other.reason, reason) || other.reason == reason));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,startDate,endDate,reason);

@override
String toString() {
  return 'BookingModificationRequest(startDate: $startDate, endDate: $endDate, reason: $reason)';
}


}

/// @nodoc
abstract mixin class $BookingModificationRequestCopyWith<$Res>  {
  factory $BookingModificationRequestCopyWith(BookingModificationRequest value, $Res Function(BookingModificationRequest) _then) = _$BookingModificationRequestCopyWithImpl;
@useResult
$Res call({
 String startDate, String endDate, String? reason
});




}
/// @nodoc
class _$BookingModificationRequestCopyWithImpl<$Res>
    implements $BookingModificationRequestCopyWith<$Res> {
  _$BookingModificationRequestCopyWithImpl(this._self, this._then);

  final BookingModificationRequest _self;
  final $Res Function(BookingModificationRequest) _then;

/// Create a copy of BookingModificationRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? startDate = null,Object? endDate = null,Object? reason = freezed,}) {
  return _then(_self.copyWith(
startDate: null == startDate ? _self.startDate : startDate // ignore: cast_nullable_to_non_nullable
as String,endDate: null == endDate ? _self.endDate : endDate // ignore: cast_nullable_to_non_nullable
as String,reason: freezed == reason ? _self.reason : reason // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [BookingModificationRequest].
extension BookingModificationRequestPatterns on BookingModificationRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _BookingModificationRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _BookingModificationRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _BookingModificationRequest value)  $default,){
final _that = this;
switch (_that) {
case _BookingModificationRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _BookingModificationRequest value)?  $default,){
final _that = this;
switch (_that) {
case _BookingModificationRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String startDate,  String endDate,  String? reason)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _BookingModificationRequest() when $default != null:
return $default(_that.startDate,_that.endDate,_that.reason);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String startDate,  String endDate,  String? reason)  $default,) {final _that = this;
switch (_that) {
case _BookingModificationRequest():
return $default(_that.startDate,_that.endDate,_that.reason);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String startDate,  String endDate,  String? reason)?  $default,) {final _that = this;
switch (_that) {
case _BookingModificationRequest() when $default != null:
return $default(_that.startDate,_that.endDate,_that.reason);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _BookingModificationRequest implements BookingModificationRequest {
  const _BookingModificationRequest({required this.startDate, required this.endDate, this.reason});
  factory _BookingModificationRequest.fromJson(Map<String, dynamic> json) => _$BookingModificationRequestFromJson(json);

@override final  String startDate;
@override final  String endDate;
@override final  String? reason;

/// Create a copy of BookingModificationRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$BookingModificationRequestCopyWith<_BookingModificationRequest> get copyWith => __$BookingModificationRequestCopyWithImpl<_BookingModificationRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$BookingModificationRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _BookingModificationRequest&&(identical(other.startDate, startDate) || other.startDate == startDate)&&(identical(other.endDate, endDate) || other.endDate == endDate)&&(identical(other.reason, reason) || other.reason == reason));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,startDate,endDate,reason);

@override
String toString() {
  return 'BookingModificationRequest(startDate: $startDate, endDate: $endDate, reason: $reason)';
}


}

/// @nodoc
abstract mixin class _$BookingModificationRequestCopyWith<$Res> implements $BookingModificationRequestCopyWith<$Res> {
  factory _$BookingModificationRequestCopyWith(_BookingModificationRequest value, $Res Function(_BookingModificationRequest) _then) = __$BookingModificationRequestCopyWithImpl;
@override @useResult
$Res call({
 String startDate, String endDate, String? reason
});




}
/// @nodoc
class __$BookingModificationRequestCopyWithImpl<$Res>
    implements _$BookingModificationRequestCopyWith<$Res> {
  __$BookingModificationRequestCopyWithImpl(this._self, this._then);

  final _BookingModificationRequest _self;
  final $Res Function(_BookingModificationRequest) _then;

/// Create a copy of BookingModificationRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? startDate = null,Object? endDate = null,Object? reason = freezed,}) {
  return _then(_BookingModificationRequest(
startDate: null == startDate ? _self.startDate : startDate // ignore: cast_nullable_to_non_nullable
as String,endDate: null == endDate ? _self.endDate : endDate // ignore: cast_nullable_to_non_nullable
as String,reason: freezed == reason ? _self.reason : reason // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
