// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'report_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ReportDto {

 String get id; UserDto? get reporter; String? get propertyId; String? get reason; String? get description; String get status; String? get resolvedAt; String get createdAt;
/// Create a copy of ReportDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ReportDtoCopyWith<ReportDto> get copyWith => _$ReportDtoCopyWithImpl<ReportDto>(this as ReportDto, _$identity);

  /// Serializes this ReportDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ReportDto&&(identical(other.id, id) || other.id == id)&&(identical(other.reporter, reporter) || other.reporter == reporter)&&(identical(other.propertyId, propertyId) || other.propertyId == propertyId)&&(identical(other.reason, reason) || other.reason == reason)&&(identical(other.description, description) || other.description == description)&&(identical(other.status, status) || other.status == status)&&(identical(other.resolvedAt, resolvedAt) || other.resolvedAt == resolvedAt)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,reporter,propertyId,reason,description,status,resolvedAt,createdAt);

@override
String toString() {
  return 'ReportDto(id: $id, reporter: $reporter, propertyId: $propertyId, reason: $reason, description: $description, status: $status, resolvedAt: $resolvedAt, createdAt: $createdAt)';
}


}

/// @nodoc
abstract mixin class $ReportDtoCopyWith<$Res>  {
  factory $ReportDtoCopyWith(ReportDto value, $Res Function(ReportDto) _then) = _$ReportDtoCopyWithImpl;
@useResult
$Res call({
 String id, UserDto? reporter, String? propertyId, String? reason, String? description, String status, String? resolvedAt, String createdAt
});


$UserDtoCopyWith<$Res>? get reporter;

}
/// @nodoc
class _$ReportDtoCopyWithImpl<$Res>
    implements $ReportDtoCopyWith<$Res> {
  _$ReportDtoCopyWithImpl(this._self, this._then);

  final ReportDto _self;
  final $Res Function(ReportDto) _then;

/// Create a copy of ReportDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? reporter = freezed,Object? propertyId = freezed,Object? reason = freezed,Object? description = freezed,Object? status = null,Object? resolvedAt = freezed,Object? createdAt = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,reporter: freezed == reporter ? _self.reporter : reporter // ignore: cast_nullable_to_non_nullable
as UserDto?,propertyId: freezed == propertyId ? _self.propertyId : propertyId // ignore: cast_nullable_to_non_nullable
as String?,reason: freezed == reason ? _self.reason : reason // ignore: cast_nullable_to_non_nullable
as String?,description: freezed == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String?,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,resolvedAt: freezed == resolvedAt ? _self.resolvedAt : resolvedAt // ignore: cast_nullable_to_non_nullable
as String?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}
/// Create a copy of ReportDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res>? get reporter {
    if (_self.reporter == null) {
    return null;
  }

  return $UserDtoCopyWith<$Res>(_self.reporter!, (value) {
    return _then(_self.copyWith(reporter: value));
  });
}
}


/// Adds pattern-matching-related methods to [ReportDto].
extension ReportDtoPatterns on ReportDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ReportDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ReportDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ReportDto value)  $default,){
final _that = this;
switch (_that) {
case _ReportDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ReportDto value)?  $default,){
final _that = this;
switch (_that) {
case _ReportDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  UserDto? reporter,  String? propertyId,  String? reason,  String? description,  String status,  String? resolvedAt,  String createdAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ReportDto() when $default != null:
return $default(_that.id,_that.reporter,_that.propertyId,_that.reason,_that.description,_that.status,_that.resolvedAt,_that.createdAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  UserDto? reporter,  String? propertyId,  String? reason,  String? description,  String status,  String? resolvedAt,  String createdAt)  $default,) {final _that = this;
switch (_that) {
case _ReportDto():
return $default(_that.id,_that.reporter,_that.propertyId,_that.reason,_that.description,_that.status,_that.resolvedAt,_that.createdAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  UserDto? reporter,  String? propertyId,  String? reason,  String? description,  String status,  String? resolvedAt,  String createdAt)?  $default,) {final _that = this;
switch (_that) {
case _ReportDto() when $default != null:
return $default(_that.id,_that.reporter,_that.propertyId,_that.reason,_that.description,_that.status,_that.resolvedAt,_that.createdAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ReportDto implements ReportDto {
  const _ReportDto({required this.id, this.reporter, this.propertyId, this.reason, this.description, required this.status, this.resolvedAt, required this.createdAt});
  factory _ReportDto.fromJson(Map<String, dynamic> json) => _$ReportDtoFromJson(json);

@override final  String id;
@override final  UserDto? reporter;
@override final  String? propertyId;
@override final  String? reason;
@override final  String? description;
@override final  String status;
@override final  String? resolvedAt;
@override final  String createdAt;

/// Create a copy of ReportDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ReportDtoCopyWith<_ReportDto> get copyWith => __$ReportDtoCopyWithImpl<_ReportDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ReportDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _ReportDto&&(identical(other.id, id) || other.id == id)&&(identical(other.reporter, reporter) || other.reporter == reporter)&&(identical(other.propertyId, propertyId) || other.propertyId == propertyId)&&(identical(other.reason, reason) || other.reason == reason)&&(identical(other.description, description) || other.description == description)&&(identical(other.status, status) || other.status == status)&&(identical(other.resolvedAt, resolvedAt) || other.resolvedAt == resolvedAt)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,reporter,propertyId,reason,description,status,resolvedAt,createdAt);

@override
String toString() {
  return 'ReportDto(id: $id, reporter: $reporter, propertyId: $propertyId, reason: $reason, description: $description, status: $status, resolvedAt: $resolvedAt, createdAt: $createdAt)';
}


}

/// @nodoc
abstract mixin class _$ReportDtoCopyWith<$Res> implements $ReportDtoCopyWith<$Res> {
  factory _$ReportDtoCopyWith(_ReportDto value, $Res Function(_ReportDto) _then) = __$ReportDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, UserDto? reporter, String? propertyId, String? reason, String? description, String status, String? resolvedAt, String createdAt
});


@override $UserDtoCopyWith<$Res>? get reporter;

}
/// @nodoc
class __$ReportDtoCopyWithImpl<$Res>
    implements _$ReportDtoCopyWith<$Res> {
  __$ReportDtoCopyWithImpl(this._self, this._then);

  final _ReportDto _self;
  final $Res Function(_ReportDto) _then;

/// Create a copy of ReportDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? reporter = freezed,Object? propertyId = freezed,Object? reason = freezed,Object? description = freezed,Object? status = null,Object? resolvedAt = freezed,Object? createdAt = null,}) {
  return _then(_ReportDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,reporter: freezed == reporter ? _self.reporter : reporter // ignore: cast_nullable_to_non_nullable
as UserDto?,propertyId: freezed == propertyId ? _self.propertyId : propertyId // ignore: cast_nullable_to_non_nullable
as String?,reason: freezed == reason ? _self.reason : reason // ignore: cast_nullable_to_non_nullable
as String?,description: freezed == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String?,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,resolvedAt: freezed == resolvedAt ? _self.resolvedAt : resolvedAt // ignore: cast_nullable_to_non_nullable
as String?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

/// Create a copy of ReportDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res>? get reporter {
    if (_self.reporter == null) {
    return null;
  }

  return $UserDtoCopyWith<$Res>(_self.reporter!, (value) {
    return _then(_self.copyWith(reporter: value));
  });
}
}

// dart format on
