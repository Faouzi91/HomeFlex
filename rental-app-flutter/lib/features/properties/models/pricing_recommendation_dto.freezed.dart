// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'pricing_recommendation_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$PricingRecommendationDto {

 String get propertyId; double get currentPrice; double get recommendedPrice; String get confidenceLevel; String get reasoning;
/// Create a copy of PricingRecommendationDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PricingRecommendationDtoCopyWith<PricingRecommendationDto> get copyWith => _$PricingRecommendationDtoCopyWithImpl<PricingRecommendationDto>(this as PricingRecommendationDto, _$identity);

  /// Serializes this PricingRecommendationDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PricingRecommendationDto&&(identical(other.propertyId, propertyId) || other.propertyId == propertyId)&&(identical(other.currentPrice, currentPrice) || other.currentPrice == currentPrice)&&(identical(other.recommendedPrice, recommendedPrice) || other.recommendedPrice == recommendedPrice)&&(identical(other.confidenceLevel, confidenceLevel) || other.confidenceLevel == confidenceLevel)&&(identical(other.reasoning, reasoning) || other.reasoning == reasoning));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,propertyId,currentPrice,recommendedPrice,confidenceLevel,reasoning);

@override
String toString() {
  return 'PricingRecommendationDto(propertyId: $propertyId, currentPrice: $currentPrice, recommendedPrice: $recommendedPrice, confidenceLevel: $confidenceLevel, reasoning: $reasoning)';
}


}

/// @nodoc
abstract mixin class $PricingRecommendationDtoCopyWith<$Res>  {
  factory $PricingRecommendationDtoCopyWith(PricingRecommendationDto value, $Res Function(PricingRecommendationDto) _then) = _$PricingRecommendationDtoCopyWithImpl;
@useResult
$Res call({
 String propertyId, double currentPrice, double recommendedPrice, String confidenceLevel, String reasoning
});




}
/// @nodoc
class _$PricingRecommendationDtoCopyWithImpl<$Res>
    implements $PricingRecommendationDtoCopyWith<$Res> {
  _$PricingRecommendationDtoCopyWithImpl(this._self, this._then);

  final PricingRecommendationDto _self;
  final $Res Function(PricingRecommendationDto) _then;

/// Create a copy of PricingRecommendationDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? propertyId = null,Object? currentPrice = null,Object? recommendedPrice = null,Object? confidenceLevel = null,Object? reasoning = null,}) {
  return _then(_self.copyWith(
propertyId: null == propertyId ? _self.propertyId : propertyId // ignore: cast_nullable_to_non_nullable
as String,currentPrice: null == currentPrice ? _self.currentPrice : currentPrice // ignore: cast_nullable_to_non_nullable
as double,recommendedPrice: null == recommendedPrice ? _self.recommendedPrice : recommendedPrice // ignore: cast_nullable_to_non_nullable
as double,confidenceLevel: null == confidenceLevel ? _self.confidenceLevel : confidenceLevel // ignore: cast_nullable_to_non_nullable
as String,reasoning: null == reasoning ? _self.reasoning : reasoning // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [PricingRecommendationDto].
extension PricingRecommendationDtoPatterns on PricingRecommendationDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _PricingRecommendationDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _PricingRecommendationDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _PricingRecommendationDto value)  $default,){
final _that = this;
switch (_that) {
case _PricingRecommendationDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _PricingRecommendationDto value)?  $default,){
final _that = this;
switch (_that) {
case _PricingRecommendationDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String propertyId,  double currentPrice,  double recommendedPrice,  String confidenceLevel,  String reasoning)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _PricingRecommendationDto() when $default != null:
return $default(_that.propertyId,_that.currentPrice,_that.recommendedPrice,_that.confidenceLevel,_that.reasoning);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String propertyId,  double currentPrice,  double recommendedPrice,  String confidenceLevel,  String reasoning)  $default,) {final _that = this;
switch (_that) {
case _PricingRecommendationDto():
return $default(_that.propertyId,_that.currentPrice,_that.recommendedPrice,_that.confidenceLevel,_that.reasoning);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String propertyId,  double currentPrice,  double recommendedPrice,  String confidenceLevel,  String reasoning)?  $default,) {final _that = this;
switch (_that) {
case _PricingRecommendationDto() when $default != null:
return $default(_that.propertyId,_that.currentPrice,_that.recommendedPrice,_that.confidenceLevel,_that.reasoning);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _PricingRecommendationDto implements PricingRecommendationDto {
  const _PricingRecommendationDto({required this.propertyId, required this.currentPrice, required this.recommendedPrice, required this.confidenceLevel, required this.reasoning});
  factory _PricingRecommendationDto.fromJson(Map<String, dynamic> json) => _$PricingRecommendationDtoFromJson(json);

@override final  String propertyId;
@override final  double currentPrice;
@override final  double recommendedPrice;
@override final  String confidenceLevel;
@override final  String reasoning;

/// Create a copy of PricingRecommendationDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$PricingRecommendationDtoCopyWith<_PricingRecommendationDto> get copyWith => __$PricingRecommendationDtoCopyWithImpl<_PricingRecommendationDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PricingRecommendationDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _PricingRecommendationDto&&(identical(other.propertyId, propertyId) || other.propertyId == propertyId)&&(identical(other.currentPrice, currentPrice) || other.currentPrice == currentPrice)&&(identical(other.recommendedPrice, recommendedPrice) || other.recommendedPrice == recommendedPrice)&&(identical(other.confidenceLevel, confidenceLevel) || other.confidenceLevel == confidenceLevel)&&(identical(other.reasoning, reasoning) || other.reasoning == reasoning));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,propertyId,currentPrice,recommendedPrice,confidenceLevel,reasoning);

@override
String toString() {
  return 'PricingRecommendationDto(propertyId: $propertyId, currentPrice: $currentPrice, recommendedPrice: $recommendedPrice, confidenceLevel: $confidenceLevel, reasoning: $reasoning)';
}


}

/// @nodoc
abstract mixin class _$PricingRecommendationDtoCopyWith<$Res> implements $PricingRecommendationDtoCopyWith<$Res> {
  factory _$PricingRecommendationDtoCopyWith(_PricingRecommendationDto value, $Res Function(_PricingRecommendationDto) _then) = __$PricingRecommendationDtoCopyWithImpl;
@override @useResult
$Res call({
 String propertyId, double currentPrice, double recommendedPrice, String confidenceLevel, String reasoning
});




}
/// @nodoc
class __$PricingRecommendationDtoCopyWithImpl<$Res>
    implements _$PricingRecommendationDtoCopyWith<$Res> {
  __$PricingRecommendationDtoCopyWithImpl(this._self, this._then);

  final _PricingRecommendationDto _self;
  final $Res Function(_PricingRecommendationDto) _then;

/// Create a copy of PricingRecommendationDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? propertyId = null,Object? currentPrice = null,Object? recommendedPrice = null,Object? confidenceLevel = null,Object? reasoning = null,}) {
  return _then(_PricingRecommendationDto(
propertyId: null == propertyId ? _self.propertyId : propertyId // ignore: cast_nullable_to_non_nullable
as String,currentPrice: null == currentPrice ? _self.currentPrice : currentPrice // ignore: cast_nullable_to_non_nullable
as double,recommendedPrice: null == recommendedPrice ? _self.recommendedPrice : recommendedPrice // ignore: cast_nullable_to_non_nullable
as double,confidenceLevel: null == confidenceLevel ? _self.confidenceLevel : confidenceLevel // ignore: cast_nullable_to_non_nullable
as String,reasoning: null == reasoning ? _self.reasoning : reasoning // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
