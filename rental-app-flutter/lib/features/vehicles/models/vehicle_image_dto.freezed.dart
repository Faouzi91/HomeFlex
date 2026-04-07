// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'vehicle_image_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$VehicleImageDto {

 String get id; String get imageUrl; int get displayOrder; bool get isPrimary;
/// Create a copy of VehicleImageDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$VehicleImageDtoCopyWith<VehicleImageDto> get copyWith => _$VehicleImageDtoCopyWithImpl<VehicleImageDto>(this as VehicleImageDto, _$identity);

  /// Serializes this VehicleImageDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is VehicleImageDto&&(identical(other.id, id) || other.id == id)&&(identical(other.imageUrl, imageUrl) || other.imageUrl == imageUrl)&&(identical(other.displayOrder, displayOrder) || other.displayOrder == displayOrder)&&(identical(other.isPrimary, isPrimary) || other.isPrimary == isPrimary));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,imageUrl,displayOrder,isPrimary);

@override
String toString() {
  return 'VehicleImageDto(id: $id, imageUrl: $imageUrl, displayOrder: $displayOrder, isPrimary: $isPrimary)';
}


}

/// @nodoc
abstract mixin class $VehicleImageDtoCopyWith<$Res>  {
  factory $VehicleImageDtoCopyWith(VehicleImageDto value, $Res Function(VehicleImageDto) _then) = _$VehicleImageDtoCopyWithImpl;
@useResult
$Res call({
 String id, String imageUrl, int displayOrder, bool isPrimary
});




}
/// @nodoc
class _$VehicleImageDtoCopyWithImpl<$Res>
    implements $VehicleImageDtoCopyWith<$Res> {
  _$VehicleImageDtoCopyWithImpl(this._self, this._then);

  final VehicleImageDto _self;
  final $Res Function(VehicleImageDto) _then;

/// Create a copy of VehicleImageDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? imageUrl = null,Object? displayOrder = null,Object? isPrimary = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,imageUrl: null == imageUrl ? _self.imageUrl : imageUrl // ignore: cast_nullable_to_non_nullable
as String,displayOrder: null == displayOrder ? _self.displayOrder : displayOrder // ignore: cast_nullable_to_non_nullable
as int,isPrimary: null == isPrimary ? _self.isPrimary : isPrimary // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}

}


/// Adds pattern-matching-related methods to [VehicleImageDto].
extension VehicleImageDtoPatterns on VehicleImageDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _VehicleImageDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _VehicleImageDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _VehicleImageDto value)  $default,){
final _that = this;
switch (_that) {
case _VehicleImageDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _VehicleImageDto value)?  $default,){
final _that = this;
switch (_that) {
case _VehicleImageDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String imageUrl,  int displayOrder,  bool isPrimary)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _VehicleImageDto() when $default != null:
return $default(_that.id,_that.imageUrl,_that.displayOrder,_that.isPrimary);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String imageUrl,  int displayOrder,  bool isPrimary)  $default,) {final _that = this;
switch (_that) {
case _VehicleImageDto():
return $default(_that.id,_that.imageUrl,_that.displayOrder,_that.isPrimary);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String imageUrl,  int displayOrder,  bool isPrimary)?  $default,) {final _that = this;
switch (_that) {
case _VehicleImageDto() when $default != null:
return $default(_that.id,_that.imageUrl,_that.displayOrder,_that.isPrimary);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _VehicleImageDto implements VehicleImageDto {
  const _VehicleImageDto({required this.id, required this.imageUrl, required this.displayOrder, required this.isPrimary});
  factory _VehicleImageDto.fromJson(Map<String, dynamic> json) => _$VehicleImageDtoFromJson(json);

@override final  String id;
@override final  String imageUrl;
@override final  int displayOrder;
@override final  bool isPrimary;

/// Create a copy of VehicleImageDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$VehicleImageDtoCopyWith<_VehicleImageDto> get copyWith => __$VehicleImageDtoCopyWithImpl<_VehicleImageDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$VehicleImageDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _VehicleImageDto&&(identical(other.id, id) || other.id == id)&&(identical(other.imageUrl, imageUrl) || other.imageUrl == imageUrl)&&(identical(other.displayOrder, displayOrder) || other.displayOrder == displayOrder)&&(identical(other.isPrimary, isPrimary) || other.isPrimary == isPrimary));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,imageUrl,displayOrder,isPrimary);

@override
String toString() {
  return 'VehicleImageDto(id: $id, imageUrl: $imageUrl, displayOrder: $displayOrder, isPrimary: $isPrimary)';
}


}

/// @nodoc
abstract mixin class _$VehicleImageDtoCopyWith<$Res> implements $VehicleImageDtoCopyWith<$Res> {
  factory _$VehicleImageDtoCopyWith(_VehicleImageDto value, $Res Function(_VehicleImageDto) _then) = __$VehicleImageDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String imageUrl, int displayOrder, bool isPrimary
});




}
/// @nodoc
class __$VehicleImageDtoCopyWithImpl<$Res>
    implements _$VehicleImageDtoCopyWith<$Res> {
  __$VehicleImageDtoCopyWithImpl(this._self, this._then);

  final _VehicleImageDto _self;
  final $Res Function(_VehicleImageDto) _then;

/// Create a copy of VehicleImageDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? imageUrl = null,Object? displayOrder = null,Object? isPrimary = null,}) {
  return _then(_VehicleImageDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,imageUrl: null == imageUrl ? _self.imageUrl : imageUrl // ignore: cast_nullable_to_non_nullable
as String,displayOrder: null == displayOrder ? _self.displayOrder : displayOrder // ignore: cast_nullable_to_non_nullable
as int,isPrimary: null == isPrimary ? _self.isPrimary : isPrimary // ignore: cast_nullable_to_non_nullable
as bool,
  ));
}


}

// dart format on
