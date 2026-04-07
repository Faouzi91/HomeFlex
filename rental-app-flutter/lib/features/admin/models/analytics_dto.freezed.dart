// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'analytics_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$AnalyticsDto {

 int get totalUsers; int get totalTenants; int get totalLandlords; int get totalProperties; int get pendingProperties; int get approvedProperties; int get totalBookings; int get pendingBookings; int get approvedBookings; int get totalMessages; Map<String, int>? get propertiesByType; Map<String, int>? get propertiesByCity; Map<String, int>? get bookingsByStatus;
/// Create a copy of AnalyticsDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$AnalyticsDtoCopyWith<AnalyticsDto> get copyWith => _$AnalyticsDtoCopyWithImpl<AnalyticsDto>(this as AnalyticsDto, _$identity);

  /// Serializes this AnalyticsDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is AnalyticsDto&&(identical(other.totalUsers, totalUsers) || other.totalUsers == totalUsers)&&(identical(other.totalTenants, totalTenants) || other.totalTenants == totalTenants)&&(identical(other.totalLandlords, totalLandlords) || other.totalLandlords == totalLandlords)&&(identical(other.totalProperties, totalProperties) || other.totalProperties == totalProperties)&&(identical(other.pendingProperties, pendingProperties) || other.pendingProperties == pendingProperties)&&(identical(other.approvedProperties, approvedProperties) || other.approvedProperties == approvedProperties)&&(identical(other.totalBookings, totalBookings) || other.totalBookings == totalBookings)&&(identical(other.pendingBookings, pendingBookings) || other.pendingBookings == pendingBookings)&&(identical(other.approvedBookings, approvedBookings) || other.approvedBookings == approvedBookings)&&(identical(other.totalMessages, totalMessages) || other.totalMessages == totalMessages)&&const DeepCollectionEquality().equals(other.propertiesByType, propertiesByType)&&const DeepCollectionEquality().equals(other.propertiesByCity, propertiesByCity)&&const DeepCollectionEquality().equals(other.bookingsByStatus, bookingsByStatus));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,totalUsers,totalTenants,totalLandlords,totalProperties,pendingProperties,approvedProperties,totalBookings,pendingBookings,approvedBookings,totalMessages,const DeepCollectionEquality().hash(propertiesByType),const DeepCollectionEquality().hash(propertiesByCity),const DeepCollectionEquality().hash(bookingsByStatus));

@override
String toString() {
  return 'AnalyticsDto(totalUsers: $totalUsers, totalTenants: $totalTenants, totalLandlords: $totalLandlords, totalProperties: $totalProperties, pendingProperties: $pendingProperties, approvedProperties: $approvedProperties, totalBookings: $totalBookings, pendingBookings: $pendingBookings, approvedBookings: $approvedBookings, totalMessages: $totalMessages, propertiesByType: $propertiesByType, propertiesByCity: $propertiesByCity, bookingsByStatus: $bookingsByStatus)';
}


}

/// @nodoc
abstract mixin class $AnalyticsDtoCopyWith<$Res>  {
  factory $AnalyticsDtoCopyWith(AnalyticsDto value, $Res Function(AnalyticsDto) _then) = _$AnalyticsDtoCopyWithImpl;
@useResult
$Res call({
 int totalUsers, int totalTenants, int totalLandlords, int totalProperties, int pendingProperties, int approvedProperties, int totalBookings, int pendingBookings, int approvedBookings, int totalMessages, Map<String, int>? propertiesByType, Map<String, int>? propertiesByCity, Map<String, int>? bookingsByStatus
});




}
/// @nodoc
class _$AnalyticsDtoCopyWithImpl<$Res>
    implements $AnalyticsDtoCopyWith<$Res> {
  _$AnalyticsDtoCopyWithImpl(this._self, this._then);

  final AnalyticsDto _self;
  final $Res Function(AnalyticsDto) _then;

/// Create a copy of AnalyticsDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? totalUsers = null,Object? totalTenants = null,Object? totalLandlords = null,Object? totalProperties = null,Object? pendingProperties = null,Object? approvedProperties = null,Object? totalBookings = null,Object? pendingBookings = null,Object? approvedBookings = null,Object? totalMessages = null,Object? propertiesByType = freezed,Object? propertiesByCity = freezed,Object? bookingsByStatus = freezed,}) {
  return _then(_self.copyWith(
totalUsers: null == totalUsers ? _self.totalUsers : totalUsers // ignore: cast_nullable_to_non_nullable
as int,totalTenants: null == totalTenants ? _self.totalTenants : totalTenants // ignore: cast_nullable_to_non_nullable
as int,totalLandlords: null == totalLandlords ? _self.totalLandlords : totalLandlords // ignore: cast_nullable_to_non_nullable
as int,totalProperties: null == totalProperties ? _self.totalProperties : totalProperties // ignore: cast_nullable_to_non_nullable
as int,pendingProperties: null == pendingProperties ? _self.pendingProperties : pendingProperties // ignore: cast_nullable_to_non_nullable
as int,approvedProperties: null == approvedProperties ? _self.approvedProperties : approvedProperties // ignore: cast_nullable_to_non_nullable
as int,totalBookings: null == totalBookings ? _self.totalBookings : totalBookings // ignore: cast_nullable_to_non_nullable
as int,pendingBookings: null == pendingBookings ? _self.pendingBookings : pendingBookings // ignore: cast_nullable_to_non_nullable
as int,approvedBookings: null == approvedBookings ? _self.approvedBookings : approvedBookings // ignore: cast_nullable_to_non_nullable
as int,totalMessages: null == totalMessages ? _self.totalMessages : totalMessages // ignore: cast_nullable_to_non_nullable
as int,propertiesByType: freezed == propertiesByType ? _self.propertiesByType : propertiesByType // ignore: cast_nullable_to_non_nullable
as Map<String, int>?,propertiesByCity: freezed == propertiesByCity ? _self.propertiesByCity : propertiesByCity // ignore: cast_nullable_to_non_nullable
as Map<String, int>?,bookingsByStatus: freezed == bookingsByStatus ? _self.bookingsByStatus : bookingsByStatus // ignore: cast_nullable_to_non_nullable
as Map<String, int>?,
  ));
}

}


/// Adds pattern-matching-related methods to [AnalyticsDto].
extension AnalyticsDtoPatterns on AnalyticsDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _AnalyticsDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _AnalyticsDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _AnalyticsDto value)  $default,){
final _that = this;
switch (_that) {
case _AnalyticsDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _AnalyticsDto value)?  $default,){
final _that = this;
switch (_that) {
case _AnalyticsDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( int totalUsers,  int totalTenants,  int totalLandlords,  int totalProperties,  int pendingProperties,  int approvedProperties,  int totalBookings,  int pendingBookings,  int approvedBookings,  int totalMessages,  Map<String, int>? propertiesByType,  Map<String, int>? propertiesByCity,  Map<String, int>? bookingsByStatus)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _AnalyticsDto() when $default != null:
return $default(_that.totalUsers,_that.totalTenants,_that.totalLandlords,_that.totalProperties,_that.pendingProperties,_that.approvedProperties,_that.totalBookings,_that.pendingBookings,_that.approvedBookings,_that.totalMessages,_that.propertiesByType,_that.propertiesByCity,_that.bookingsByStatus);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( int totalUsers,  int totalTenants,  int totalLandlords,  int totalProperties,  int pendingProperties,  int approvedProperties,  int totalBookings,  int pendingBookings,  int approvedBookings,  int totalMessages,  Map<String, int>? propertiesByType,  Map<String, int>? propertiesByCity,  Map<String, int>? bookingsByStatus)  $default,) {final _that = this;
switch (_that) {
case _AnalyticsDto():
return $default(_that.totalUsers,_that.totalTenants,_that.totalLandlords,_that.totalProperties,_that.pendingProperties,_that.approvedProperties,_that.totalBookings,_that.pendingBookings,_that.approvedBookings,_that.totalMessages,_that.propertiesByType,_that.propertiesByCity,_that.bookingsByStatus);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( int totalUsers,  int totalTenants,  int totalLandlords,  int totalProperties,  int pendingProperties,  int approvedProperties,  int totalBookings,  int pendingBookings,  int approvedBookings,  int totalMessages,  Map<String, int>? propertiesByType,  Map<String, int>? propertiesByCity,  Map<String, int>? bookingsByStatus)?  $default,) {final _that = this;
switch (_that) {
case _AnalyticsDto() when $default != null:
return $default(_that.totalUsers,_that.totalTenants,_that.totalLandlords,_that.totalProperties,_that.pendingProperties,_that.approvedProperties,_that.totalBookings,_that.pendingBookings,_that.approvedBookings,_that.totalMessages,_that.propertiesByType,_that.propertiesByCity,_that.bookingsByStatus);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _AnalyticsDto implements AnalyticsDto {
  const _AnalyticsDto({this.totalUsers = 0, this.totalTenants = 0, this.totalLandlords = 0, this.totalProperties = 0, this.pendingProperties = 0, this.approvedProperties = 0, this.totalBookings = 0, this.pendingBookings = 0, this.approvedBookings = 0, this.totalMessages = 0, final  Map<String, int>? propertiesByType, final  Map<String, int>? propertiesByCity, final  Map<String, int>? bookingsByStatus}): _propertiesByType = propertiesByType,_propertiesByCity = propertiesByCity,_bookingsByStatus = bookingsByStatus;
  factory _AnalyticsDto.fromJson(Map<String, dynamic> json) => _$AnalyticsDtoFromJson(json);

@override@JsonKey() final  int totalUsers;
@override@JsonKey() final  int totalTenants;
@override@JsonKey() final  int totalLandlords;
@override@JsonKey() final  int totalProperties;
@override@JsonKey() final  int pendingProperties;
@override@JsonKey() final  int approvedProperties;
@override@JsonKey() final  int totalBookings;
@override@JsonKey() final  int pendingBookings;
@override@JsonKey() final  int approvedBookings;
@override@JsonKey() final  int totalMessages;
 final  Map<String, int>? _propertiesByType;
@override Map<String, int>? get propertiesByType {
  final value = _propertiesByType;
  if (value == null) return null;
  if (_propertiesByType is EqualUnmodifiableMapView) return _propertiesByType;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableMapView(value);
}

 final  Map<String, int>? _propertiesByCity;
@override Map<String, int>? get propertiesByCity {
  final value = _propertiesByCity;
  if (value == null) return null;
  if (_propertiesByCity is EqualUnmodifiableMapView) return _propertiesByCity;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableMapView(value);
}

 final  Map<String, int>? _bookingsByStatus;
@override Map<String, int>? get bookingsByStatus {
  final value = _bookingsByStatus;
  if (value == null) return null;
  if (_bookingsByStatus is EqualUnmodifiableMapView) return _bookingsByStatus;
  // ignore: implicit_dynamic_type
  return EqualUnmodifiableMapView(value);
}


/// Create a copy of AnalyticsDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$AnalyticsDtoCopyWith<_AnalyticsDto> get copyWith => __$AnalyticsDtoCopyWithImpl<_AnalyticsDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$AnalyticsDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _AnalyticsDto&&(identical(other.totalUsers, totalUsers) || other.totalUsers == totalUsers)&&(identical(other.totalTenants, totalTenants) || other.totalTenants == totalTenants)&&(identical(other.totalLandlords, totalLandlords) || other.totalLandlords == totalLandlords)&&(identical(other.totalProperties, totalProperties) || other.totalProperties == totalProperties)&&(identical(other.pendingProperties, pendingProperties) || other.pendingProperties == pendingProperties)&&(identical(other.approvedProperties, approvedProperties) || other.approvedProperties == approvedProperties)&&(identical(other.totalBookings, totalBookings) || other.totalBookings == totalBookings)&&(identical(other.pendingBookings, pendingBookings) || other.pendingBookings == pendingBookings)&&(identical(other.approvedBookings, approvedBookings) || other.approvedBookings == approvedBookings)&&(identical(other.totalMessages, totalMessages) || other.totalMessages == totalMessages)&&const DeepCollectionEquality().equals(other._propertiesByType, _propertiesByType)&&const DeepCollectionEquality().equals(other._propertiesByCity, _propertiesByCity)&&const DeepCollectionEquality().equals(other._bookingsByStatus, _bookingsByStatus));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,totalUsers,totalTenants,totalLandlords,totalProperties,pendingProperties,approvedProperties,totalBookings,pendingBookings,approvedBookings,totalMessages,const DeepCollectionEquality().hash(_propertiesByType),const DeepCollectionEquality().hash(_propertiesByCity),const DeepCollectionEquality().hash(_bookingsByStatus));

@override
String toString() {
  return 'AnalyticsDto(totalUsers: $totalUsers, totalTenants: $totalTenants, totalLandlords: $totalLandlords, totalProperties: $totalProperties, pendingProperties: $pendingProperties, approvedProperties: $approvedProperties, totalBookings: $totalBookings, pendingBookings: $pendingBookings, approvedBookings: $approvedBookings, totalMessages: $totalMessages, propertiesByType: $propertiesByType, propertiesByCity: $propertiesByCity, bookingsByStatus: $bookingsByStatus)';
}


}

/// @nodoc
abstract mixin class _$AnalyticsDtoCopyWith<$Res> implements $AnalyticsDtoCopyWith<$Res> {
  factory _$AnalyticsDtoCopyWith(_AnalyticsDto value, $Res Function(_AnalyticsDto) _then) = __$AnalyticsDtoCopyWithImpl;
@override @useResult
$Res call({
 int totalUsers, int totalTenants, int totalLandlords, int totalProperties, int pendingProperties, int approvedProperties, int totalBookings, int pendingBookings, int approvedBookings, int totalMessages, Map<String, int>? propertiesByType, Map<String, int>? propertiesByCity, Map<String, int>? bookingsByStatus
});




}
/// @nodoc
class __$AnalyticsDtoCopyWithImpl<$Res>
    implements _$AnalyticsDtoCopyWith<$Res> {
  __$AnalyticsDtoCopyWithImpl(this._self, this._then);

  final _AnalyticsDto _self;
  final $Res Function(_AnalyticsDto) _then;

/// Create a copy of AnalyticsDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? totalUsers = null,Object? totalTenants = null,Object? totalLandlords = null,Object? totalProperties = null,Object? pendingProperties = null,Object? approvedProperties = null,Object? totalBookings = null,Object? pendingBookings = null,Object? approvedBookings = null,Object? totalMessages = null,Object? propertiesByType = freezed,Object? propertiesByCity = freezed,Object? bookingsByStatus = freezed,}) {
  return _then(_AnalyticsDto(
totalUsers: null == totalUsers ? _self.totalUsers : totalUsers // ignore: cast_nullable_to_non_nullable
as int,totalTenants: null == totalTenants ? _self.totalTenants : totalTenants // ignore: cast_nullable_to_non_nullable
as int,totalLandlords: null == totalLandlords ? _self.totalLandlords : totalLandlords // ignore: cast_nullable_to_non_nullable
as int,totalProperties: null == totalProperties ? _self.totalProperties : totalProperties // ignore: cast_nullable_to_non_nullable
as int,pendingProperties: null == pendingProperties ? _self.pendingProperties : pendingProperties // ignore: cast_nullable_to_non_nullable
as int,approvedProperties: null == approvedProperties ? _self.approvedProperties : approvedProperties // ignore: cast_nullable_to_non_nullable
as int,totalBookings: null == totalBookings ? _self.totalBookings : totalBookings // ignore: cast_nullable_to_non_nullable
as int,pendingBookings: null == pendingBookings ? _self.pendingBookings : pendingBookings // ignore: cast_nullable_to_non_nullable
as int,approvedBookings: null == approvedBookings ? _self.approvedBookings : approvedBookings // ignore: cast_nullable_to_non_nullable
as int,totalMessages: null == totalMessages ? _self.totalMessages : totalMessages // ignore: cast_nullable_to_non_nullable
as int,propertiesByType: freezed == propertiesByType ? _self._propertiesByType : propertiesByType // ignore: cast_nullable_to_non_nullable
as Map<String, int>?,propertiesByCity: freezed == propertiesByCity ? _self._propertiesByCity : propertiesByCity // ignore: cast_nullable_to_non_nullable
as Map<String, int>?,bookingsByStatus: freezed == bookingsByStatus ? _self._bookingsByStatus : bookingsByStatus // ignore: cast_nullable_to_non_nullable
as Map<String, int>?,
  ));
}


}

// dart format on
