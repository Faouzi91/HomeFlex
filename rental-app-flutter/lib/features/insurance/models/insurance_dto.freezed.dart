// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'insurance_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$InsurancePlanDto {

 String get id; String get providerName; String get name; String get type; String get description; String get coverageDetails; double get dailyPremium; double? get maxCoverageAmount;
/// Create a copy of InsurancePlanDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$InsurancePlanDtoCopyWith<InsurancePlanDto> get copyWith => _$InsurancePlanDtoCopyWithImpl<InsurancePlanDto>(this as InsurancePlanDto, _$identity);

  /// Serializes this InsurancePlanDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is InsurancePlanDto&&(identical(other.id, id) || other.id == id)&&(identical(other.providerName, providerName) || other.providerName == providerName)&&(identical(other.name, name) || other.name == name)&&(identical(other.type, type) || other.type == type)&&(identical(other.description, description) || other.description == description)&&(identical(other.coverageDetails, coverageDetails) || other.coverageDetails == coverageDetails)&&(identical(other.dailyPremium, dailyPremium) || other.dailyPremium == dailyPremium)&&(identical(other.maxCoverageAmount, maxCoverageAmount) || other.maxCoverageAmount == maxCoverageAmount));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,providerName,name,type,description,coverageDetails,dailyPremium,maxCoverageAmount);

@override
String toString() {
  return 'InsurancePlanDto(id: $id, providerName: $providerName, name: $name, type: $type, description: $description, coverageDetails: $coverageDetails, dailyPremium: $dailyPremium, maxCoverageAmount: $maxCoverageAmount)';
}


}

/// @nodoc
abstract mixin class $InsurancePlanDtoCopyWith<$Res>  {
  factory $InsurancePlanDtoCopyWith(InsurancePlanDto value, $Res Function(InsurancePlanDto) _then) = _$InsurancePlanDtoCopyWithImpl;
@useResult
$Res call({
 String id, String providerName, String name, String type, String description, String coverageDetails, double dailyPremium, double? maxCoverageAmount
});




}
/// @nodoc
class _$InsurancePlanDtoCopyWithImpl<$Res>
    implements $InsurancePlanDtoCopyWith<$Res> {
  _$InsurancePlanDtoCopyWithImpl(this._self, this._then);

  final InsurancePlanDto _self;
  final $Res Function(InsurancePlanDto) _then;

/// Create a copy of InsurancePlanDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? providerName = null,Object? name = null,Object? type = null,Object? description = null,Object? coverageDetails = null,Object? dailyPremium = null,Object? maxCoverageAmount = freezed,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,providerName: null == providerName ? _self.providerName : providerName // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,description: null == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String,coverageDetails: null == coverageDetails ? _self.coverageDetails : coverageDetails // ignore: cast_nullable_to_non_nullable
as String,dailyPremium: null == dailyPremium ? _self.dailyPremium : dailyPremium // ignore: cast_nullable_to_non_nullable
as double,maxCoverageAmount: freezed == maxCoverageAmount ? _self.maxCoverageAmount : maxCoverageAmount // ignore: cast_nullable_to_non_nullable
as double?,
  ));
}

}


/// Adds pattern-matching-related methods to [InsurancePlanDto].
extension InsurancePlanDtoPatterns on InsurancePlanDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _InsurancePlanDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _InsurancePlanDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _InsurancePlanDto value)  $default,){
final _that = this;
switch (_that) {
case _InsurancePlanDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _InsurancePlanDto value)?  $default,){
final _that = this;
switch (_that) {
case _InsurancePlanDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String providerName,  String name,  String type,  String description,  String coverageDetails,  double dailyPremium,  double? maxCoverageAmount)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _InsurancePlanDto() when $default != null:
return $default(_that.id,_that.providerName,_that.name,_that.type,_that.description,_that.coverageDetails,_that.dailyPremium,_that.maxCoverageAmount);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String providerName,  String name,  String type,  String description,  String coverageDetails,  double dailyPremium,  double? maxCoverageAmount)  $default,) {final _that = this;
switch (_that) {
case _InsurancePlanDto():
return $default(_that.id,_that.providerName,_that.name,_that.type,_that.description,_that.coverageDetails,_that.dailyPremium,_that.maxCoverageAmount);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String providerName,  String name,  String type,  String description,  String coverageDetails,  double dailyPremium,  double? maxCoverageAmount)?  $default,) {final _that = this;
switch (_that) {
case _InsurancePlanDto() when $default != null:
return $default(_that.id,_that.providerName,_that.name,_that.type,_that.description,_that.coverageDetails,_that.dailyPremium,_that.maxCoverageAmount);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _InsurancePlanDto implements InsurancePlanDto {
  const _InsurancePlanDto({required this.id, required this.providerName, required this.name, required this.type, required this.description, required this.coverageDetails, required this.dailyPremium, this.maxCoverageAmount});
  factory _InsurancePlanDto.fromJson(Map<String, dynamic> json) => _$InsurancePlanDtoFromJson(json);

@override final  String id;
@override final  String providerName;
@override final  String name;
@override final  String type;
@override final  String description;
@override final  String coverageDetails;
@override final  double dailyPremium;
@override final  double? maxCoverageAmount;

/// Create a copy of InsurancePlanDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$InsurancePlanDtoCopyWith<_InsurancePlanDto> get copyWith => __$InsurancePlanDtoCopyWithImpl<_InsurancePlanDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$InsurancePlanDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _InsurancePlanDto&&(identical(other.id, id) || other.id == id)&&(identical(other.providerName, providerName) || other.providerName == providerName)&&(identical(other.name, name) || other.name == name)&&(identical(other.type, type) || other.type == type)&&(identical(other.description, description) || other.description == description)&&(identical(other.coverageDetails, coverageDetails) || other.coverageDetails == coverageDetails)&&(identical(other.dailyPremium, dailyPremium) || other.dailyPremium == dailyPremium)&&(identical(other.maxCoverageAmount, maxCoverageAmount) || other.maxCoverageAmount == maxCoverageAmount));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,providerName,name,type,description,coverageDetails,dailyPremium,maxCoverageAmount);

@override
String toString() {
  return 'InsurancePlanDto(id: $id, providerName: $providerName, name: $name, type: $type, description: $description, coverageDetails: $coverageDetails, dailyPremium: $dailyPremium, maxCoverageAmount: $maxCoverageAmount)';
}


}

/// @nodoc
abstract mixin class _$InsurancePlanDtoCopyWith<$Res> implements $InsurancePlanDtoCopyWith<$Res> {
  factory _$InsurancePlanDtoCopyWith(_InsurancePlanDto value, $Res Function(_InsurancePlanDto) _then) = __$InsurancePlanDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String providerName, String name, String type, String description, String coverageDetails, double dailyPremium, double? maxCoverageAmount
});




}
/// @nodoc
class __$InsurancePlanDtoCopyWithImpl<$Res>
    implements _$InsurancePlanDtoCopyWith<$Res> {
  __$InsurancePlanDtoCopyWithImpl(this._self, this._then);

  final _InsurancePlanDto _self;
  final $Res Function(_InsurancePlanDto) _then;

/// Create a copy of InsurancePlanDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? providerName = null,Object? name = null,Object? type = null,Object? description = null,Object? coverageDetails = null,Object? dailyPremium = null,Object? maxCoverageAmount = freezed,}) {
  return _then(_InsurancePlanDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,providerName: null == providerName ? _self.providerName : providerName // ignore: cast_nullable_to_non_nullable
as String,name: null == name ? _self.name : name // ignore: cast_nullable_to_non_nullable
as String,type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as String,description: null == description ? _self.description : description // ignore: cast_nullable_to_non_nullable
as String,coverageDetails: null == coverageDetails ? _self.coverageDetails : coverageDetails // ignore: cast_nullable_to_non_nullable
as String,dailyPremium: null == dailyPremium ? _self.dailyPremium : dailyPremium // ignore: cast_nullable_to_non_nullable
as double,maxCoverageAmount: freezed == maxCoverageAmount ? _self.maxCoverageAmount : maxCoverageAmount // ignore: cast_nullable_to_non_nullable
as double?,
  ));
}


}


/// @nodoc
mixin _$InsurancePolicyDto {

 String get id; InsurancePlanDto get plan; UserDto get user; BookingDto? get booking; String get policyNumber; String get status; String get startDate; String get endDate; double get totalPremium; String? get certificateUrl; String get createdAt;
/// Create a copy of InsurancePolicyDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$InsurancePolicyDtoCopyWith<InsurancePolicyDto> get copyWith => _$InsurancePolicyDtoCopyWithImpl<InsurancePolicyDto>(this as InsurancePolicyDto, _$identity);

  /// Serializes this InsurancePolicyDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is InsurancePolicyDto&&(identical(other.id, id) || other.id == id)&&(identical(other.plan, plan) || other.plan == plan)&&(identical(other.user, user) || other.user == user)&&(identical(other.booking, booking) || other.booking == booking)&&(identical(other.policyNumber, policyNumber) || other.policyNumber == policyNumber)&&(identical(other.status, status) || other.status == status)&&(identical(other.startDate, startDate) || other.startDate == startDate)&&(identical(other.endDate, endDate) || other.endDate == endDate)&&(identical(other.totalPremium, totalPremium) || other.totalPremium == totalPremium)&&(identical(other.certificateUrl, certificateUrl) || other.certificateUrl == certificateUrl)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,plan,user,booking,policyNumber,status,startDate,endDate,totalPremium,certificateUrl,createdAt);

@override
String toString() {
  return 'InsurancePolicyDto(id: $id, plan: $plan, user: $user, booking: $booking, policyNumber: $policyNumber, status: $status, startDate: $startDate, endDate: $endDate, totalPremium: $totalPremium, certificateUrl: $certificateUrl, createdAt: $createdAt)';
}


}

/// @nodoc
abstract mixin class $InsurancePolicyDtoCopyWith<$Res>  {
  factory $InsurancePolicyDtoCopyWith(InsurancePolicyDto value, $Res Function(InsurancePolicyDto) _then) = _$InsurancePolicyDtoCopyWithImpl;
@useResult
$Res call({
 String id, InsurancePlanDto plan, UserDto user, BookingDto? booking, String policyNumber, String status, String startDate, String endDate, double totalPremium, String? certificateUrl, String createdAt
});


$InsurancePlanDtoCopyWith<$Res> get plan;$UserDtoCopyWith<$Res> get user;$BookingDtoCopyWith<$Res>? get booking;

}
/// @nodoc
class _$InsurancePolicyDtoCopyWithImpl<$Res>
    implements $InsurancePolicyDtoCopyWith<$Res> {
  _$InsurancePolicyDtoCopyWithImpl(this._self, this._then);

  final InsurancePolicyDto _self;
  final $Res Function(InsurancePolicyDto) _then;

/// Create a copy of InsurancePolicyDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? plan = null,Object? user = null,Object? booking = freezed,Object? policyNumber = null,Object? status = null,Object? startDate = null,Object? endDate = null,Object? totalPremium = null,Object? certificateUrl = freezed,Object? createdAt = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,plan: null == plan ? _self.plan : plan // ignore: cast_nullable_to_non_nullable
as InsurancePlanDto,user: null == user ? _self.user : user // ignore: cast_nullable_to_non_nullable
as UserDto,booking: freezed == booking ? _self.booking : booking // ignore: cast_nullable_to_non_nullable
as BookingDto?,policyNumber: null == policyNumber ? _self.policyNumber : policyNumber // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,startDate: null == startDate ? _self.startDate : startDate // ignore: cast_nullable_to_non_nullable
as String,endDate: null == endDate ? _self.endDate : endDate // ignore: cast_nullable_to_non_nullable
as String,totalPremium: null == totalPremium ? _self.totalPremium : totalPremium // ignore: cast_nullable_to_non_nullable
as double,certificateUrl: freezed == certificateUrl ? _self.certificateUrl : certificateUrl // ignore: cast_nullable_to_non_nullable
as String?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}
/// Create a copy of InsurancePolicyDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$InsurancePlanDtoCopyWith<$Res> get plan {
  
  return $InsurancePlanDtoCopyWith<$Res>(_self.plan, (value) {
    return _then(_self.copyWith(plan: value));
  });
}/// Create a copy of InsurancePolicyDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res> get user {
  
  return $UserDtoCopyWith<$Res>(_self.user, (value) {
    return _then(_self.copyWith(user: value));
  });
}/// Create a copy of InsurancePolicyDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$BookingDtoCopyWith<$Res>? get booking {
    if (_self.booking == null) {
    return null;
  }

  return $BookingDtoCopyWith<$Res>(_self.booking!, (value) {
    return _then(_self.copyWith(booking: value));
  });
}
}


/// Adds pattern-matching-related methods to [InsurancePolicyDto].
extension InsurancePolicyDtoPatterns on InsurancePolicyDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _InsurancePolicyDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _InsurancePolicyDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _InsurancePolicyDto value)  $default,){
final _that = this;
switch (_that) {
case _InsurancePolicyDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _InsurancePolicyDto value)?  $default,){
final _that = this;
switch (_that) {
case _InsurancePolicyDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  InsurancePlanDto plan,  UserDto user,  BookingDto? booking,  String policyNumber,  String status,  String startDate,  String endDate,  double totalPremium,  String? certificateUrl,  String createdAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _InsurancePolicyDto() when $default != null:
return $default(_that.id,_that.plan,_that.user,_that.booking,_that.policyNumber,_that.status,_that.startDate,_that.endDate,_that.totalPremium,_that.certificateUrl,_that.createdAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  InsurancePlanDto plan,  UserDto user,  BookingDto? booking,  String policyNumber,  String status,  String startDate,  String endDate,  double totalPremium,  String? certificateUrl,  String createdAt)  $default,) {final _that = this;
switch (_that) {
case _InsurancePolicyDto():
return $default(_that.id,_that.plan,_that.user,_that.booking,_that.policyNumber,_that.status,_that.startDate,_that.endDate,_that.totalPremium,_that.certificateUrl,_that.createdAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  InsurancePlanDto plan,  UserDto user,  BookingDto? booking,  String policyNumber,  String status,  String startDate,  String endDate,  double totalPremium,  String? certificateUrl,  String createdAt)?  $default,) {final _that = this;
switch (_that) {
case _InsurancePolicyDto() when $default != null:
return $default(_that.id,_that.plan,_that.user,_that.booking,_that.policyNumber,_that.status,_that.startDate,_that.endDate,_that.totalPremium,_that.certificateUrl,_that.createdAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _InsurancePolicyDto implements InsurancePolicyDto {
  const _InsurancePolicyDto({required this.id, required this.plan, required this.user, this.booking, required this.policyNumber, required this.status, required this.startDate, required this.endDate, required this.totalPremium, this.certificateUrl, required this.createdAt});
  factory _InsurancePolicyDto.fromJson(Map<String, dynamic> json) => _$InsurancePolicyDtoFromJson(json);

@override final  String id;
@override final  InsurancePlanDto plan;
@override final  UserDto user;
@override final  BookingDto? booking;
@override final  String policyNumber;
@override final  String status;
@override final  String startDate;
@override final  String endDate;
@override final  double totalPremium;
@override final  String? certificateUrl;
@override final  String createdAt;

/// Create a copy of InsurancePolicyDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$InsurancePolicyDtoCopyWith<_InsurancePolicyDto> get copyWith => __$InsurancePolicyDtoCopyWithImpl<_InsurancePolicyDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$InsurancePolicyDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _InsurancePolicyDto&&(identical(other.id, id) || other.id == id)&&(identical(other.plan, plan) || other.plan == plan)&&(identical(other.user, user) || other.user == user)&&(identical(other.booking, booking) || other.booking == booking)&&(identical(other.policyNumber, policyNumber) || other.policyNumber == policyNumber)&&(identical(other.status, status) || other.status == status)&&(identical(other.startDate, startDate) || other.startDate == startDate)&&(identical(other.endDate, endDate) || other.endDate == endDate)&&(identical(other.totalPremium, totalPremium) || other.totalPremium == totalPremium)&&(identical(other.certificateUrl, certificateUrl) || other.certificateUrl == certificateUrl)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,plan,user,booking,policyNumber,status,startDate,endDate,totalPremium,certificateUrl,createdAt);

@override
String toString() {
  return 'InsurancePolicyDto(id: $id, plan: $plan, user: $user, booking: $booking, policyNumber: $policyNumber, status: $status, startDate: $startDate, endDate: $endDate, totalPremium: $totalPremium, certificateUrl: $certificateUrl, createdAt: $createdAt)';
}


}

/// @nodoc
abstract mixin class _$InsurancePolicyDtoCopyWith<$Res> implements $InsurancePolicyDtoCopyWith<$Res> {
  factory _$InsurancePolicyDtoCopyWith(_InsurancePolicyDto value, $Res Function(_InsurancePolicyDto) _then) = __$InsurancePolicyDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, InsurancePlanDto plan, UserDto user, BookingDto? booking, String policyNumber, String status, String startDate, String endDate, double totalPremium, String? certificateUrl, String createdAt
});


@override $InsurancePlanDtoCopyWith<$Res> get plan;@override $UserDtoCopyWith<$Res> get user;@override $BookingDtoCopyWith<$Res>? get booking;

}
/// @nodoc
class __$InsurancePolicyDtoCopyWithImpl<$Res>
    implements _$InsurancePolicyDtoCopyWith<$Res> {
  __$InsurancePolicyDtoCopyWithImpl(this._self, this._then);

  final _InsurancePolicyDto _self;
  final $Res Function(_InsurancePolicyDto) _then;

/// Create a copy of InsurancePolicyDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? plan = null,Object? user = null,Object? booking = freezed,Object? policyNumber = null,Object? status = null,Object? startDate = null,Object? endDate = null,Object? totalPremium = null,Object? certificateUrl = freezed,Object? createdAt = null,}) {
  return _then(_InsurancePolicyDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,plan: null == plan ? _self.plan : plan // ignore: cast_nullable_to_non_nullable
as InsurancePlanDto,user: null == user ? _self.user : user // ignore: cast_nullable_to_non_nullable
as UserDto,booking: freezed == booking ? _self.booking : booking // ignore: cast_nullable_to_non_nullable
as BookingDto?,policyNumber: null == policyNumber ? _self.policyNumber : policyNumber // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,startDate: null == startDate ? _self.startDate : startDate // ignore: cast_nullable_to_non_nullable
as String,endDate: null == endDate ? _self.endDate : endDate // ignore: cast_nullable_to_non_nullable
as String,totalPremium: null == totalPremium ? _self.totalPremium : totalPremium // ignore: cast_nullable_to_non_nullable
as double,certificateUrl: freezed == certificateUrl ? _self.certificateUrl : certificateUrl // ignore: cast_nullable_to_non_nullable
as String?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

/// Create a copy of InsurancePolicyDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$InsurancePlanDtoCopyWith<$Res> get plan {
  
  return $InsurancePlanDtoCopyWith<$Res>(_self.plan, (value) {
    return _then(_self.copyWith(plan: value));
  });
}/// Create a copy of InsurancePolicyDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res> get user {
  
  return $UserDtoCopyWith<$Res>(_self.user, (value) {
    return _then(_self.copyWith(user: value));
  });
}/// Create a copy of InsurancePolicyDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$BookingDtoCopyWith<$Res>? get booking {
    if (_self.booking == null) {
    return null;
  }

  return $BookingDtoCopyWith<$Res>(_self.booking!, (value) {
    return _then(_self.copyWith(booking: value));
  });
}
}

// dart format on
