// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'amenity_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AmenityDto {

 String get id; String get name; String get nameFr; String? get icon; String get category;
/// Create a copy of AmenityDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AmenityDtoCopyWith<AmenityDto> get copyWith => _$AmenityDtoCopyWithImpl<AmenityDto>(this as AmenityDto, _$identity);

  /// Serializes this AmenityDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AmenityDto&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.nameFr, nameFr) || other.nameFr == nameFr)&&(identical(other.icon, icon) || other.icon == icon)&&(identical(other.category, category) || other.category == category));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,name,nameFr,icon,category);

@override
String toString() {
  return 'AmenityDto(id: $id, name: $name, nameFr: $nameFr, icon: $icon, category: $category)';
}


}

/// @nodoc
abstract mixin class $AmenityDtoCopyWith<$Res>  {
  factory $AmenityDtoCopyWith(AmenityDto value, $Res Function(AmenityDto) _then) = _$AmenityDtoCopyWithImpl;
@useResult
$Res call({
 String id, String name, String nameFr, String? icon, String category
});




}
/// @nodoc
class _$AmenityDtoCopyWithImpl<$Res>
    implements $AmenityDtoCopyWith<$Res> {
  _$AmenityDtoCopyWithImpl(this._self, this._then);

  final AmenityDto _self;
  final $Res Function(AmenityDto) _then;

/// Create a copy of AmenityDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? name = null,Object? nameFr = null,Object? icon = freezed,Object? category = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,nameFr: null == nameFr ? _self.nameFr : nameFr // ignore: cast_nullable_to_non_nullable
as String,icon: freezed == icon ? _self.icon : icon // ignore: cast_nullable_to_non_nullable
as String?,category: null == category ? _self.category : category // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [AmenityDto].
extension AmenityDtoPatterns on AmenityDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AmenityDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AmenityDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AmenityDto value)  $default,){
final _that = this;
switch (_that) {
case _AmenityDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AmenityDto value)?  $default,){
final _that = this;
switch (_that) {
case _AmenityDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String name,  String nameFr,  String? icon,  String category)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AmenityDto() when $default != null:
return $default(_that.id,_that.name,_that.nameFr,_that.icon,_that.category);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String name,  String nameFr,  String? icon,  String category)  $default,) {final _that = this;
switch (_that) {
case _AmenityDto():
return $default(_that.id,_that.name,_that.nameFr,_that.icon,_that.category);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String name,  String nameFr,  String? icon,  String category)?  $default,) {final _that = this;
switch (_that) {
case _AmenityDto() when $default != null:
return $default(_that.id,_that.name,_that.nameFr,_that.icon,_that.category);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AmenityDto implements AmenityDto {
  const _AmenityDto({required this.id, required this.name, required this.nameFr, this.icon, required this.category});
  factory _AmenityDto.fromJson(Map<String, dynamic> json) => _$AmenityDtoFromJson(json);

@override final  String id;
@override final  String name;
@override final  String nameFr;
@override final  String? icon;
@override final  String category;

/// Create a copy of AmenityDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AmenityDtoCopyWith<_AmenityDto> get copyWith => __$AmenityDtoCopyWithImpl<_AmenityDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AmenityDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AmenityDto&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.nameFr, nameFr) || other.nameFr == nameFr)&&(identical(other.icon, icon) || other.icon == icon)&&(identical(other.category, category) || other.category == category));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,name,nameFr,icon,category);

@override
String toString() {
  return 'AmenityDto(id: $id, name: $name, nameFr: $nameFr, icon: $icon, category: $category)';
}


}

/// @nodoc
abstract mixin class _$AmenityDtoCopyWith<$Res> implements $AmenityDtoCopyWith<$Res> {
  factory _$AmenityDtoCopyWith(_AmenityDto value, $Res Function(_AmenityDto) _then) = __$AmenityDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String name, String nameFr, String? icon, String category
});




}
/// @nodoc
class __$AmenityDtoCopyWithImpl<$Res>
    implements _$AmenityDtoCopyWith<$Res> {
  __$AmenityDtoCopyWithImpl(this._self, this._then);

  final _AmenityDto _self;
  final $Res Function(_AmenityDto) _then;

/// Create a copy of AmenityDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? name = null,Object? nameFr = null,Object? icon = freezed,Object? category = null,}) {
  return _then(_AmenityDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,nameFr: null == nameFr ? _self.nameFr : nameFr // ignore: cast_nullable_to_non_nullable
as String,icon: freezed == icon ? _self.icon : icon // ignore: cast_nullable_to_non_nullable
as String?,category: null == category ? _self.category : category // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
