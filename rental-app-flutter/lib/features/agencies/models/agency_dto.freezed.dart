// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'agency_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AgencyDto {

 String get id; String get name; String? get description; String? get logoUrl; String? get websiteUrl; String get email; String? get phoneNumber; String? get address; bool get isVerified; String? get customDomain; String get themePrimaryColor; String get createdAt;
/// Create a copy of AgencyDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AgencyDtoCopyWith<AgencyDto> get copyWith => _$AgencyDtoCopyWithImpl<AgencyDto>(this as AgencyDto, _$identity);

  /// Serializes this AgencyDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AgencyDto&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.description, description) || other.description == description)&&(identical(other.logoUrl, logoUrl) || other.logoUrl == logoUrl)&&(identical(other.websiteUrl, websiteUrl) || other.websiteUrl == websiteUrl)&&(identical(other.email, email) || other.email == email)&&(identical(other.phoneNumber, phoneNumber) || other.phoneNumber == phoneNumber)&&(identical(other.address, address) || other.address == address)&&(identical(other.isVerified, isVerified) || other.isVerified == isVerified)&&(identical(other.customDomain, customDomain) || other.customDomain == customDomain)&&(identical(other.themePrimaryColor, themePrimaryColor) || other.themePrimaryColor == themePrimaryColor)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,name,description,logoUrl,websiteUrl,email,phoneNumber,address,isVerified,customDomain,themePrimaryColor,createdAt);

@override
String toString() {
  return 'AgencyDto(id: $id, name: $name, description: $description, logoUrl: $logoUrl, websiteUrl: $websiteUrl, email: $email, phoneNumber: $phoneNumber, address: $address, isVerified: $isVerified, customDomain: $customDomain, themePrimaryColor: $themePrimaryColor, createdAt: $createdAt)';
}


}

/// @nodoc
abstract mixin class $AgencyDtoCopyWith<$Res>  {
  factory $AgencyDtoCopyWith(AgencyDto value, $Res Function(AgencyDto) _then) = _$AgencyDtoCopyWithImpl;
@useResult
$Res call({
 String id, String name, String? description, String? logoUrl, String? websiteUrl, String email, String? phoneNumber, String? address, bool isVerified, String? customDomain, String themePrimaryColor, String createdAt
});




}
/// @nodoc
class _$AgencyDtoCopyWithImpl<$Res>
    implements $AgencyDtoCopyWith<$Res> {
  _$AgencyDtoCopyWithImpl(this._self, this._then);

  final AgencyDto _self;
  final $Res Function(AgencyDto) _then;

/// Create a copy of AgencyDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? name = null,Object? description = freezed,Object? logoUrl = freezed,Object? websiteUrl = freezed,Object? email = null,Object? phoneNumber = freezed,Object? address = freezed,Object? isVerified = null,Object? customDomain = freezed,Object? themePrimaryColor = null,Object? createdAt = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,description: freezed == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String?,logoUrl: freezed == logoUrl ? _self.logoUrl : logoUrl // ignore: cast_nullable_to_non_nullable
as String?,websiteUrl: freezed == websiteUrl ? _self.websiteUrl : websiteUrl // ignore: cast_nullable_to_non_nullable
as String?,email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,phoneNumber: freezed == phoneNumber ? _self.phoneNumber : phoneNumber // ignore: cast_nullable_to_non_nullable
as String?,address: freezed == address ? _self.address : address // ignore: cast_nullable_to_non_nullable
as String?,isVerified: null == isVerified ? _self.isVerified : isVerified // ignore: cast_nullable_to_non_nullable
as bool,customDomain: freezed == customDomain ? _self.customDomain : customDomain // ignore: cast_nullable_to_non_nullable
as String?,themePrimaryColor: null == themePrimaryColor ? _self.themePrimaryColor : themePrimaryColor // ignore: cast_nullable_to_non_nullable
as String,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [AgencyDto].
extension AgencyDtoPatterns on AgencyDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AgencyDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AgencyDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AgencyDto value)  $default,){
final _that = this;
switch (_that) {
case _AgencyDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AgencyDto value)?  $default,){
final _that = this;
switch (_that) {
case _AgencyDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String name,  String? description,  String? logoUrl,  String? websiteUrl,  String email,  String? phoneNumber,  String? address,  bool isVerified,  String? customDomain,  String themePrimaryColor,  String createdAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AgencyDto() when $default != null:
return $default(_that.id,_that.name,_that.description,_that.logoUrl,_that.websiteUrl,_that.email,_that.phoneNumber,_that.address,_that.isVerified,_that.customDomain,_that.themePrimaryColor,_that.createdAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String name,  String? description,  String? logoUrl,  String? websiteUrl,  String email,  String? phoneNumber,  String? address,  bool isVerified,  String? customDomain,  String themePrimaryColor,  String createdAt)  $default,) {final _that = this;
switch (_that) {
case _AgencyDto():
return $default(_that.id,_that.name,_that.description,_that.logoUrl,_that.websiteUrl,_that.email,_that.phoneNumber,_that.address,_that.isVerified,_that.customDomain,_that.themePrimaryColor,_that.createdAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String name,  String? description,  String? logoUrl,  String? websiteUrl,  String email,  String? phoneNumber,  String? address,  bool isVerified,  String? customDomain,  String themePrimaryColor,  String createdAt)?  $default,) {final _that = this;
switch (_that) {
case _AgencyDto() when $default != null:
return $default(_that.id,_that.name,_that.description,_that.logoUrl,_that.websiteUrl,_that.email,_that.phoneNumber,_that.address,_that.isVerified,_that.customDomain,_that.themePrimaryColor,_that.createdAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AgencyDto implements AgencyDto {
  const _AgencyDto({required this.id, required this.name, this.description, this.logoUrl, this.websiteUrl, required this.email, this.phoneNumber, this.address, required this.isVerified, this.customDomain, required this.themePrimaryColor, required this.createdAt});
  factory _AgencyDto.fromJson(Map<String, dynamic> json) => _$AgencyDtoFromJson(json);

@override final  String id;
@override final  String name;
@override final  String? description;
@override final  String? logoUrl;
@override final  String? websiteUrl;
@override final  String email;
@override final  String? phoneNumber;
@override final  String? address;
@override final  bool isVerified;
@override final  String? customDomain;
@override final  String themePrimaryColor;
@override final  String createdAt;

/// Create a copy of AgencyDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AgencyDtoCopyWith<_AgencyDto> get copyWith => __$AgencyDtoCopyWithImpl<_AgencyDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AgencyDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AgencyDto&&(identical(other.id, id) || other.id == id)&&(identical(other.name, name) || other.name == name)&&(identical(other.description, description) || other.description == description)&&(identical(other.logoUrl, logoUrl) || other.logoUrl == logoUrl)&&(identical(other.websiteUrl, websiteUrl) || other.websiteUrl == websiteUrl)&&(identical(other.email, email) || other.email == email)&&(identical(other.phoneNumber, phoneNumber) || other.phoneNumber == phoneNumber)&&(identical(other.address, address) || other.address == address)&&(identical(other.isVerified, isVerified) || other.isVerified == isVerified)&&(identical(other.customDomain, customDomain) || other.customDomain == customDomain)&&(identical(other.themePrimaryColor, themePrimaryColor) || other.themePrimaryColor == themePrimaryColor)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,name,description,logoUrl,websiteUrl,email,phoneNumber,address,isVerified,customDomain,themePrimaryColor,createdAt);

@override
String toString() {
  return 'AgencyDto(id: $id, name: $name, description: $description, logoUrl: $logoUrl, websiteUrl: $websiteUrl, email: $email, phoneNumber: $phoneNumber, address: $address, isVerified: $isVerified, customDomain: $customDomain, themePrimaryColor: $themePrimaryColor, createdAt: $createdAt)';
}


}

/// @nodoc
abstract mixin class _$AgencyDtoCopyWith<$Res> implements $AgencyDtoCopyWith<$Res> {
  factory _$AgencyDtoCopyWith(_AgencyDto value, $Res Function(_AgencyDto) _then) = __$AgencyDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String name, String? description, String? logoUrl, String? websiteUrl, String email, String? phoneNumber, String? address, bool isVerified, String? customDomain, String themePrimaryColor, String createdAt
});




}
/// @nodoc
class __$AgencyDtoCopyWithImpl<$Res>
    implements _$AgencyDtoCopyWith<$Res> {
  __$AgencyDtoCopyWithImpl(this._self, this._then);

  final _AgencyDto _self;
  final $Res Function(_AgencyDto) _then;

/// Create a copy of AgencyDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? name = null,Object? description = freezed,Object? logoUrl = freezed,Object? websiteUrl = freezed,Object? email = null,Object? phoneNumber = freezed,Object? address = freezed,Object? isVerified = null,Object? customDomain = freezed,Object? themePrimaryColor = null,Object? createdAt = null,}) {
  return _then(_AgencyDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,description: freezed == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String?,logoUrl: freezed == logoUrl ? _self.logoUrl : logoUrl // ignore: cast_nullable_to_non_nullable
as String?,websiteUrl: freezed == websiteUrl ? _self.websiteUrl : websiteUrl // ignore: cast_nullable_to_non_nullable
as String?,email: null == email ? _self.email : email // ignore: cast_nullable_to_non_nullable
as String,phoneNumber: freezed == phoneNumber ? _self.phoneNumber : phoneNumber // ignore: cast_nullable_to_non_nullable
as String?,address: freezed == address ? _self.address : address // ignore: cast_nullable_to_non_nullable
as String?,isVerified: null == isVerified ? _self.isVerified : isVerified // ignore: cast_nullable_to_non_nullable
as bool,customDomain: freezed == customDomain ? _self.customDomain : customDomain // ignore: cast_nullable_to_non_nullable
as String?,themePrimaryColor: null == themePrimaryColor ? _self.themePrimaryColor : themePrimaryColor // ignore: cast_nullable_to_non_nullable
as String,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
