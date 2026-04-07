// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'property_video_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$PropertyVideoDto {

 String get id; String get videoUrl; String? get thumbnailUrl; int? get durationSeconds;
/// Create a copy of PropertyVideoDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PropertyVideoDtoCopyWith<PropertyVideoDto> get copyWith => _$PropertyVideoDtoCopyWithImpl<PropertyVideoDto>(this as PropertyVideoDto, _$identity);

  /// Serializes this PropertyVideoDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PropertyVideoDto&&(identical(other.id, id) || other.id == id)&&(identical(other.videoUrl, videoUrl) || other.videoUrl == videoUrl)&&(identical(other.thumbnailUrl, thumbnailUrl) || other.thumbnailUrl == thumbnailUrl)&&(identical(other.durationSeconds, durationSeconds) || other.durationSeconds == durationSeconds));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,videoUrl,thumbnailUrl,durationSeconds);

@override
String toString() {
  return 'PropertyVideoDto(id: $id, videoUrl: $videoUrl, thumbnailUrl: $thumbnailUrl, durationSeconds: $durationSeconds)';
}


}

/// @nodoc
abstract mixin class $PropertyVideoDtoCopyWith<$Res>  {
  factory $PropertyVideoDtoCopyWith(PropertyVideoDto value, $Res Function(PropertyVideoDto) _then) = _$PropertyVideoDtoCopyWithImpl;
@useResult
$Res call({
 String id, String videoUrl, String? thumbnailUrl, int? durationSeconds
});




}
/// @nodoc
class _$PropertyVideoDtoCopyWithImpl<$Res>
    implements $PropertyVideoDtoCopyWith<$Res> {
  _$PropertyVideoDtoCopyWithImpl(this._self, this._then);

  final PropertyVideoDto _self;
  final $Res Function(PropertyVideoDto) _then;

/// Create a copy of PropertyVideoDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? videoUrl = null,Object? thumbnailUrl = freezed,Object? durationSeconds = freezed,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,videoUrl: null == videoUrl ? _self.videoUrl : videoUrl // ignore: cast_nullable_to_non_nullable
as String,thumbnailUrl: freezed == thumbnailUrl ? _self.thumbnailUrl : thumbnailUrl // ignore: cast_nullable_to_non_nullable
as String?,durationSeconds: freezed == durationSeconds ? _self.durationSeconds : durationSeconds // ignore: cast_nullable_to_non_nullable
as int?,
  ));
}

}


/// Adds pattern-matching-related methods to [PropertyVideoDto].
extension PropertyVideoDtoPatterns on PropertyVideoDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _PropertyVideoDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _PropertyVideoDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _PropertyVideoDto value)  $default,){
final _that = this;
switch (_that) {
case _PropertyVideoDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _PropertyVideoDto value)?  $default,){
final _that = this;
switch (_that) {
case _PropertyVideoDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String videoUrl,  String? thumbnailUrl,  int? durationSeconds)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _PropertyVideoDto() when $default != null:
return $default(_that.id,_that.videoUrl,_that.thumbnailUrl,_that.durationSeconds);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String videoUrl,  String? thumbnailUrl,  int? durationSeconds)  $default,) {final _that = this;
switch (_that) {
case _PropertyVideoDto():
return $default(_that.id,_that.videoUrl,_that.thumbnailUrl,_that.durationSeconds);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String videoUrl,  String? thumbnailUrl,  int? durationSeconds)?  $default,) {final _that = this;
switch (_that) {
case _PropertyVideoDto() when $default != null:
return $default(_that.id,_that.videoUrl,_that.thumbnailUrl,_that.durationSeconds);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _PropertyVideoDto implements PropertyVideoDto {
  const _PropertyVideoDto({required this.id, required this.videoUrl, this.thumbnailUrl, this.durationSeconds});
  factory _PropertyVideoDto.fromJson(Map<String, dynamic> json) => _$PropertyVideoDtoFromJson(json);

@override final  String id;
@override final  String videoUrl;
@override final  String? thumbnailUrl;
@override final  int? durationSeconds;

/// Create a copy of PropertyVideoDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$PropertyVideoDtoCopyWith<_PropertyVideoDto> get copyWith => __$PropertyVideoDtoCopyWithImpl<_PropertyVideoDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PropertyVideoDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _PropertyVideoDto&&(identical(other.id, id) || other.id == id)&&(identical(other.videoUrl, videoUrl) || other.videoUrl == videoUrl)&&(identical(other.thumbnailUrl, thumbnailUrl) || other.thumbnailUrl == thumbnailUrl)&&(identical(other.durationSeconds, durationSeconds) || other.durationSeconds == durationSeconds));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,videoUrl,thumbnailUrl,durationSeconds);

@override
String toString() {
  return 'PropertyVideoDto(id: $id, videoUrl: $videoUrl, thumbnailUrl: $thumbnailUrl, durationSeconds: $durationSeconds)';
}


}

/// @nodoc
abstract mixin class _$PropertyVideoDtoCopyWith<$Res> implements $PropertyVideoDtoCopyWith<$Res> {
  factory _$PropertyVideoDtoCopyWith(_PropertyVideoDto value, $Res Function(_PropertyVideoDto) _then) = __$PropertyVideoDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String videoUrl, String? thumbnailUrl, int? durationSeconds
});




}
/// @nodoc
class __$PropertyVideoDtoCopyWithImpl<$Res>
    implements _$PropertyVideoDtoCopyWith<$Res> {
  __$PropertyVideoDtoCopyWithImpl(this._self, this._then);

  final _PropertyVideoDto _self;
  final $Res Function(_PropertyVideoDto) _then;

/// Create a copy of PropertyVideoDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? videoUrl = null,Object? thumbnailUrl = freezed,Object? durationSeconds = freezed,}) {
  return _then(_PropertyVideoDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,videoUrl: null == videoUrl ? _self.videoUrl : videoUrl // ignore: cast_nullable_to_non_nullable
as String,thumbnailUrl: freezed == thumbnailUrl ? _self.thumbnailUrl : thumbnailUrl // ignore: cast_nullable_to_non_nullable
as String?,durationSeconds: freezed == durationSeconds ? _self.durationSeconds : durationSeconds // ignore: cast_nullable_to_non_nullable
as int?,
  ));
}


}

// dart format on
