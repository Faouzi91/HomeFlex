// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'property_image_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$PropertyImageDto {

 String get id; String get imageUrl; String? get thumbnailUrl; int? get displayOrder; bool? get isPrimary;
/// Create a copy of PropertyImageDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PropertyImageDtoCopyWith<PropertyImageDto> get copyWith => _$PropertyImageDtoCopyWithImpl<PropertyImageDto>(this as PropertyImageDto, _$identity);

  /// Serializes this PropertyImageDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PropertyImageDto&&(identical(other.id, id) || other.id == id)&&(identical(other.imageUrl, imageUrl) || other.imageUrl == imageUrl)&&(identical(other.thumbnailUrl, thumbnailUrl) || other.thumbnailUrl == thumbnailUrl)&&(identical(other.displayOrder, displayOrder) || other.displayOrder == displayOrder)&&(identical(other.isPrimary, isPrimary) || other.isPrimary == isPrimary));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,imageUrl,thumbnailUrl,displayOrder,isPrimary);

@override
String toString() {
  return 'PropertyImageDto(id: $id, imageUrl: $imageUrl, thumbnailUrl: $thumbnailUrl, displayOrder: $displayOrder, isPrimary: $isPrimary)';
}


}

/// @nodoc
abstract mixin class $PropertyImageDtoCopyWith<$Res>  {
  factory $PropertyImageDtoCopyWith(PropertyImageDto value, $Res Function(PropertyImageDto) _then) = _$PropertyImageDtoCopyWithImpl;
@useResult
$Res call({
 String id, String imageUrl, String? thumbnailUrl, int? displayOrder, bool? isPrimary
});




}
/// @nodoc
class _$PropertyImageDtoCopyWithImpl<$Res>
    implements $PropertyImageDtoCopyWith<$Res> {
  _$PropertyImageDtoCopyWithImpl(this._self, this._then);

  final PropertyImageDto _self;
  final $Res Function(PropertyImageDto) _then;

/// Create a copy of PropertyImageDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? imageUrl = null,Object? thumbnailUrl = freezed,Object? displayOrder = freezed,Object? isPrimary = freezed,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,imageUrl: null == imageUrl ? _self.imageUrl : imageUrl // ignore: cast_nullable_to_non_nullable
as String,thumbnailUrl: freezed == thumbnailUrl ? _self.thumbnailUrl : thumbnailUrl // ignore: cast_nullable_to_non_nullable
as String?,displayOrder: freezed == displayOrder ? _self.displayOrder : displayOrder // ignore: cast_nullable_to_non_nullable
as int?,isPrimary: freezed == isPrimary ? _self.isPrimary : isPrimary // ignore: cast_nullable_to_non_nullable
as bool?,
  ));
}

}


/// Adds pattern-matching-related methods to [PropertyImageDto].
extension PropertyImageDtoPatterns on PropertyImageDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _PropertyImageDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _PropertyImageDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _PropertyImageDto value)  $default,){
final _that = this;
switch (_that) {
case _PropertyImageDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _PropertyImageDto value)?  $default,){
final _that = this;
switch (_that) {
case _PropertyImageDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String imageUrl,  String? thumbnailUrl,  int? displayOrder,  bool? isPrimary)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _PropertyImageDto() when $default != null:
return $default(_that.id,_that.imageUrl,_that.thumbnailUrl,_that.displayOrder,_that.isPrimary);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String imageUrl,  String? thumbnailUrl,  int? displayOrder,  bool? isPrimary)  $default,) {final _that = this;
switch (_that) {
case _PropertyImageDto():
return $default(_that.id,_that.imageUrl,_that.thumbnailUrl,_that.displayOrder,_that.isPrimary);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String imageUrl,  String? thumbnailUrl,  int? displayOrder,  bool? isPrimary)?  $default,) {final _that = this;
switch (_that) {
case _PropertyImageDto() when $default != null:
return $default(_that.id,_that.imageUrl,_that.thumbnailUrl,_that.displayOrder,_that.isPrimary);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _PropertyImageDto implements PropertyImageDto {
  const _PropertyImageDto({required this.id, required this.imageUrl, this.thumbnailUrl, this.displayOrder, this.isPrimary});
  factory _PropertyImageDto.fromJson(Map<String, dynamic> json) => _$PropertyImageDtoFromJson(json);

@override final  String id;
@override final  String imageUrl;
@override final  String? thumbnailUrl;
@override final  int? displayOrder;
@override final  bool? isPrimary;

/// Create a copy of PropertyImageDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$PropertyImageDtoCopyWith<_PropertyImageDto> get copyWith => __$PropertyImageDtoCopyWithImpl<_PropertyImageDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PropertyImageDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _PropertyImageDto&&(identical(other.id, id) || other.id == id)&&(identical(other.imageUrl, imageUrl) || other.imageUrl == imageUrl)&&(identical(other.thumbnailUrl, thumbnailUrl) || other.thumbnailUrl == thumbnailUrl)&&(identical(other.displayOrder, displayOrder) || other.displayOrder == displayOrder)&&(identical(other.isPrimary, isPrimary) || other.isPrimary == isPrimary));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,imageUrl,thumbnailUrl,displayOrder,isPrimary);

@override
String toString() {
  return 'PropertyImageDto(id: $id, imageUrl: $imageUrl, thumbnailUrl: $thumbnailUrl, displayOrder: $displayOrder, isPrimary: $isPrimary)';
}


}

/// @nodoc
abstract mixin class _$PropertyImageDtoCopyWith<$Res> implements $PropertyImageDtoCopyWith<$Res> {
  factory _$PropertyImageDtoCopyWith(_PropertyImageDto value, $Res Function(_PropertyImageDto) _then) = __$PropertyImageDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String imageUrl, String? thumbnailUrl, int? displayOrder, bool? isPrimary
});




}
/// @nodoc
class __$PropertyImageDtoCopyWithImpl<$Res>
    implements _$PropertyImageDtoCopyWith<$Res> {
  __$PropertyImageDtoCopyWithImpl(this._self, this._then);

  final _PropertyImageDto _self;
  final $Res Function(_PropertyImageDto) _then;

/// Create a copy of PropertyImageDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? imageUrl = null,Object? thumbnailUrl = freezed,Object? displayOrder = freezed,Object? isPrimary = freezed,}) {
  return _then(_PropertyImageDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,imageUrl: null == imageUrl ? _self.imageUrl : imageUrl // ignore: cast_nullable_to_non_nullable
as String,thumbnailUrl: freezed == thumbnailUrl ? _self.thumbnailUrl : thumbnailUrl // ignore: cast_nullable_to_non_nullable
as String?,displayOrder: freezed == displayOrder ? _self.displayOrder : displayOrder // ignore: cast_nullable_to_non_nullable
as int?,isPrimary: freezed == isPrimary ? _self.isPrimary : isPrimary // ignore: cast_nullable_to_non_nullable
as bool?,
  ));
}


}

// dart format on
