// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'dispute_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$DisputeDto {

 String get id; String get bookingId; String get initiatorId; String get reason; String? get description; String get status; String? get resolutionNotes; String? get resolvedAt; String? get resolvedById; String get createdAt; String get updatedAt;
/// Create a copy of DisputeDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$DisputeDtoCopyWith<DisputeDto> get copyWith => _$DisputeDtoCopyWithImpl<DisputeDto>(this as DisputeDto, _$identity);

  /// Serializes this DisputeDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is DisputeDto&&(identical(other.id, id) || other.id == id)&&(identical(other.bookingId, bookingId) || other.bookingId == bookingId)&&(identical(other.initiatorId, initiatorId) || other.initiatorId == initiatorId)&&(identical(other.reason, reason) || other.reason == reason)&&(identical(other.description, description) || other.description == description)&&(identical(other.status, status) || other.status == status)&&(identical(other.resolutionNotes, resolutionNotes) || other.resolutionNotes == resolutionNotes)&&(identical(other.resolvedAt, resolvedAt) || other.resolvedAt == resolvedAt)&&(identical(other.resolvedById, resolvedById) || other.resolvedById == resolvedById)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.updatedAt, updatedAt) || other.updatedAt == updatedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,bookingId,initiatorId,reason,description,status,resolutionNotes,resolvedAt,resolvedById,createdAt,updatedAt);

@override
String toString() {
  return 'DisputeDto(id: $id, bookingId: $bookingId, initiatorId: $initiatorId, reason: $reason, description: $description, status: $status, resolutionNotes: $resolutionNotes, resolvedAt: $resolvedAt, resolvedById: $resolvedById, createdAt: $createdAt, updatedAt: $updatedAt)';
}


}

/// @nodoc
abstract mixin class $DisputeDtoCopyWith<$Res>  {
  factory $DisputeDtoCopyWith(DisputeDto value, $Res Function(DisputeDto) _then) = _$DisputeDtoCopyWithImpl;
@useResult
$Res call({
 String id, String bookingId, String initiatorId, String reason, String? description, String status, String? resolutionNotes, String? resolvedAt, String? resolvedById, String createdAt, String updatedAt
});




}
/// @nodoc
class _$DisputeDtoCopyWithImpl<$Res>
    implements $DisputeDtoCopyWith<$Res> {
  _$DisputeDtoCopyWithImpl(this._self, this._then);

  final DisputeDto _self;
  final $Res Function(DisputeDto) _then;

/// Create a copy of DisputeDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? bookingId = null,Object? initiatorId = null,Object? reason = null,Object? description = freezed,Object? status = null,Object? resolutionNotes = freezed,Object? resolvedAt = freezed,Object? resolvedById = freezed,Object? createdAt = null,Object? updatedAt = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,bookingId: null == bookingId ? _self.bookingId : bookingId // ignore: cast_nullable_to_non_nullable
as String,initiatorId: null == initiatorId ? _self.initiatorId : initiatorId // ignore: cast_nullable_to_non_nullable
as String,reason: null == reason ? _self.reason : reason // ignore: cast_nullable_to_non_nullable
as String,description: freezed == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String?,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,resolutionNotes: freezed == resolutionNotes ? _self.resolutionNotes : resolutionNotes // ignore: cast_nullable_to_non_nullable
as String?,resolvedAt: freezed == resolvedAt ? _self.resolvedAt : resolvedAt // ignore: cast_nullable_to_non_nullable
as String?,resolvedById: freezed == resolvedById ? _self.resolvedById : resolvedById // ignore: cast_nullable_to_non_nullable
as String?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,updatedAt: null == updatedAt ? _self.updatedAt : updatedAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [DisputeDto].
extension DisputeDtoPatterns on DisputeDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _DisputeDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _DisputeDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _DisputeDto value)  $default,){
final _that = this;
switch (_that) {
case _DisputeDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _DisputeDto value)?  $default,){
final _that = this;
switch (_that) {
case _DisputeDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String bookingId,  String initiatorId,  String reason,  String? description,  String status,  String? resolutionNotes,  String? resolvedAt,  String? resolvedById,  String createdAt,  String updatedAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _DisputeDto() when $default != null:
return $default(_that.id,_that.bookingId,_that.initiatorId,_that.reason,_that.description,_that.status,_that.resolutionNotes,_that.resolvedAt,_that.resolvedById,_that.createdAt,_that.updatedAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String bookingId,  String initiatorId,  String reason,  String? description,  String status,  String? resolutionNotes,  String? resolvedAt,  String? resolvedById,  String createdAt,  String updatedAt)  $default,) {final _that = this;
switch (_that) {
case _DisputeDto():
return $default(_that.id,_that.bookingId,_that.initiatorId,_that.reason,_that.description,_that.status,_that.resolutionNotes,_that.resolvedAt,_that.resolvedById,_that.createdAt,_that.updatedAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String bookingId,  String initiatorId,  String reason,  String? description,  String status,  String? resolutionNotes,  String? resolvedAt,  String? resolvedById,  String createdAt,  String updatedAt)?  $default,) {final _that = this;
switch (_that) {
case _DisputeDto() when $default != null:
return $default(_that.id,_that.bookingId,_that.initiatorId,_that.reason,_that.description,_that.status,_that.resolutionNotes,_that.resolvedAt,_that.resolvedById,_that.createdAt,_that.updatedAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _DisputeDto implements DisputeDto {
  const _DisputeDto({required this.id, required this.bookingId, required this.initiatorId, required this.reason, this.description, required this.status, this.resolutionNotes, this.resolvedAt, this.resolvedById, required this.createdAt, required this.updatedAt});
  factory _DisputeDto.fromJson(Map<String, dynamic> json) => _$DisputeDtoFromJson(json);

@override final  String id;
@override final  String bookingId;
@override final  String initiatorId;
@override final  String reason;
@override final  String? description;
@override final  String status;
@override final  String? resolutionNotes;
@override final  String? resolvedAt;
@override final  String? resolvedById;
@override final  String createdAt;
@override final  String updatedAt;

/// Create a copy of DisputeDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$DisputeDtoCopyWith<_DisputeDto> get copyWith => __$DisputeDtoCopyWithImpl<_DisputeDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$DisputeDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _DisputeDto&&(identical(other.id, id) || other.id == id)&&(identical(other.bookingId, bookingId) || other.bookingId == bookingId)&&(identical(other.initiatorId, initiatorId) || other.initiatorId == initiatorId)&&(identical(other.reason, reason) || other.reason == reason)&&(identical(other.description, description) || other.description == description)&&(identical(other.status, status) || other.status == status)&&(identical(other.resolutionNotes, resolutionNotes) || other.resolutionNotes == resolutionNotes)&&(identical(other.resolvedAt, resolvedAt) || other.resolvedAt == resolvedAt)&&(identical(other.resolvedById, resolvedById) || other.resolvedById == resolvedById)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.updatedAt, updatedAt) || other.updatedAt == updatedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,bookingId,initiatorId,reason,description,status,resolutionNotes,resolvedAt,resolvedById,createdAt,updatedAt);

@override
String toString() {
  return 'DisputeDto(id: $id, bookingId: $bookingId, initiatorId: $initiatorId, reason: $reason, description: $description, status: $status, resolutionNotes: $resolutionNotes, resolvedAt: $resolvedAt, resolvedById: $resolvedById, createdAt: $createdAt, updatedAt: $updatedAt)';
}


}

/// @nodoc
abstract mixin class _$DisputeDtoCopyWith<$Res> implements $DisputeDtoCopyWith<$Res> {
  factory _$DisputeDtoCopyWith(_DisputeDto value, $Res Function(_DisputeDto) _then) = __$DisputeDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String bookingId, String initiatorId, String reason, String? description, String status, String? resolutionNotes, String? resolvedAt, String? resolvedById, String createdAt, String updatedAt
});




}
/// @nodoc
class __$DisputeDtoCopyWithImpl<$Res>
    implements _$DisputeDtoCopyWith<$Res> {
  __$DisputeDtoCopyWithImpl(this._self, this._then);

  final _DisputeDto _self;
  final $Res Function(_DisputeDto) _then;

/// Create a copy of DisputeDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? bookingId = null,Object? initiatorId = null,Object? reason = null,Object? description = freezed,Object? status = null,Object? resolutionNotes = freezed,Object? resolvedAt = freezed,Object? resolvedById = freezed,Object? createdAt = null,Object? updatedAt = null,}) {
  return _then(_DisputeDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,bookingId: null == bookingId ? _self.bookingId : bookingId // ignore: cast_nullable_to_non_nullable
as String,initiatorId: null == initiatorId ? _self.initiatorId : initiatorId // ignore: cast_nullable_to_non_nullable
as String,reason: null == reason ? _self.reason : reason // ignore: cast_nullable_to_non_nullable
as String,description: freezed == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String?,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,resolutionNotes: freezed == resolutionNotes ? _self.resolutionNotes : resolutionNotes // ignore: cast_nullable_to_non_nullable
as String?,resolvedAt: freezed == resolvedAt ? _self.resolvedAt : resolvedAt // ignore: cast_nullable_to_non_nullable
as String?,resolvedById: freezed == resolvedById ? _self.resolvedById : resolvedById // ignore: cast_nullable_to_non_nullable
as String?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,updatedAt: null == updatedAt ? _self.updatedAt : updatedAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
