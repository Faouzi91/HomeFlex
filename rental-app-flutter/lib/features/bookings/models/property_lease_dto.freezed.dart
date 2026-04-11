// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'property_lease_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$PropertyLeaseDto {

 String get id; String get bookingId; String get tenantId; String get landlordId; String get content; String get status; String? get signedAt; String? get blockchainTxHash; String get onChainStatus; String? get contractAddress; String? get tokenId; String get createdAt; String get updatedAt;
/// Create a copy of PropertyLeaseDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$PropertyLeaseDtoCopyWith<PropertyLeaseDto> get copyWith => _$PropertyLeaseDtoCopyWithImpl<PropertyLeaseDto>(this as PropertyLeaseDto, _$identity);

  /// Serializes this PropertyLeaseDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is PropertyLeaseDto&&(identical(other.id, id) || other.id == id)&&(identical(other.bookingId, bookingId) || other.bookingId == bookingId)&&(identical(other.tenantId, tenantId) || other.tenantId == tenantId)&&(identical(other.landlordId, landlordId) || other.landlordId == landlordId)&&(identical(other.content, content) || other.content == content)&&(identical(other.status, status) || other.status == status)&&(identical(other.signedAt, signedAt) || other.signedAt == signedAt)&&(identical(other.blockchainTxHash, blockchainTxHash) || other.blockchainTxHash == blockchainTxHash)&&(identical(other.onChainStatus, onChainStatus) || other.onChainStatus == onChainStatus)&&(identical(other.contractAddress, contractAddress) || other.contractAddress == contractAddress)&&(identical(other.tokenId, tokenId) || other.tokenId == tokenId)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.updatedAt, updatedAt) || other.updatedAt == updatedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,bookingId,tenantId,landlordId,content,status,signedAt,blockchainTxHash,onChainStatus,contractAddress,tokenId,createdAt,updatedAt);

@override
String toString() {
  return 'PropertyLeaseDto(id: $id, bookingId: $bookingId, tenantId: $tenantId, landlordId: $landlordId, content: $content, status: $status, signedAt: $signedAt, blockchainTxHash: $blockchainTxHash, onChainStatus: $onChainStatus, contractAddress: $contractAddress, tokenId: $tokenId, createdAt: $createdAt, updatedAt: $updatedAt)';
}


}

/// @nodoc
abstract mixin class $PropertyLeaseDtoCopyWith<$Res>  {
  factory $PropertyLeaseDtoCopyWith(PropertyLeaseDto value, $Res Function(PropertyLeaseDto) _then) = _$PropertyLeaseDtoCopyWithImpl;
@useResult
$Res call({
 String id, String bookingId, String tenantId, String landlordId, String content, String status, String? signedAt, String? blockchainTxHash, String onChainStatus, String? contractAddress, String? tokenId, String createdAt, String updatedAt
});




}
/// @nodoc
class _$PropertyLeaseDtoCopyWithImpl<$Res>
    implements $PropertyLeaseDtoCopyWith<$Res> {
  _$PropertyLeaseDtoCopyWithImpl(this._self, this._then);

  final PropertyLeaseDto _self;
  final $Res Function(PropertyLeaseDto) _then;

/// Create a copy of PropertyLeaseDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? bookingId = null,Object? tenantId = null,Object? landlordId = null,Object? content = null,Object? status = null,Object? signedAt = freezed,Object? blockchainTxHash = freezed,Object? onChainStatus = null,Object? contractAddress = freezed,Object? tokenId = freezed,Object? createdAt = null,Object? updatedAt = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,bookingId: null == bookingId ? _self.bookingId : bookingId // ignore: cast_nullable_to_non_nullable
as String,tenantId: null == tenantId ? _self.tenantId : tenantId // ignore: cast_nullable_to_non_nullable
as String,landlordId: null == landlordId ? _self.landlordId : landlordId // ignore: cast_nullable_to_non_nullable
as String,content: null == content ? _self.content : content // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,signedAt: freezed == signedAt ? _self.signedAt : signedAt // ignore: cast_nullable_to_non_nullable
as String?,blockchainTxHash: freezed == blockchainTxHash ? _self.blockchainTxHash : blockchainTxHash // ignore: cast_nullable_to_non_nullable
as String?,onChainStatus: null == onChainStatus ? _self.onChainStatus : onChainStatus // ignore: cast_nullable_to_non_nullable
as String,contractAddress: freezed == contractAddress ? _self.contractAddress : contractAddress // ignore: cast_nullable_to_non_nullable
as String?,tokenId: freezed == tokenId ? _self.tokenId : tokenId // ignore: cast_nullable_to_non_nullable
as String?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,updatedAt: null == updatedAt ? _self.updatedAt : updatedAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [PropertyLeaseDto].
extension PropertyLeaseDtoPatterns on PropertyLeaseDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _PropertyLeaseDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _PropertyLeaseDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _PropertyLeaseDto value)  $default,){
final _that = this;
switch (_that) {
case _PropertyLeaseDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _PropertyLeaseDto value)?  $default,){
final _that = this;
switch (_that) {
case _PropertyLeaseDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String bookingId,  String tenantId,  String landlordId,  String content,  String status,  String? signedAt,  String? blockchainTxHash,  String onChainStatus,  String? contractAddress,  String? tokenId,  String createdAt,  String updatedAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _PropertyLeaseDto() when $default != null:
return $default(_that.id,_that.bookingId,_that.tenantId,_that.landlordId,_that.content,_that.status,_that.signedAt,_that.blockchainTxHash,_that.onChainStatus,_that.contractAddress,_that.tokenId,_that.createdAt,_that.updatedAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String bookingId,  String tenantId,  String landlordId,  String content,  String status,  String? signedAt,  String? blockchainTxHash,  String onChainStatus,  String? contractAddress,  String? tokenId,  String createdAt,  String updatedAt)  $default,) {final _that = this;
switch (_that) {
case _PropertyLeaseDto():
return $default(_that.id,_that.bookingId,_that.tenantId,_that.landlordId,_that.content,_that.status,_that.signedAt,_that.blockchainTxHash,_that.onChainStatus,_that.contractAddress,_that.tokenId,_that.createdAt,_that.updatedAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String bookingId,  String tenantId,  String landlordId,  String content,  String status,  String? signedAt,  String? blockchainTxHash,  String onChainStatus,  String? contractAddress,  String? tokenId,  String createdAt,  String updatedAt)?  $default,) {final _that = this;
switch (_that) {
case _PropertyLeaseDto() when $default != null:
return $default(_that.id,_that.bookingId,_that.tenantId,_that.landlordId,_that.content,_that.status,_that.signedAt,_that.blockchainTxHash,_that.onChainStatus,_that.contractAddress,_that.tokenId,_that.createdAt,_that.updatedAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _PropertyLeaseDto implements PropertyLeaseDto {
  const _PropertyLeaseDto({required this.id, required this.bookingId, required this.tenantId, required this.landlordId, required this.content, required this.status, this.signedAt, this.blockchainTxHash, required this.onChainStatus, this.contractAddress, this.tokenId, required this.createdAt, required this.updatedAt});
  factory _PropertyLeaseDto.fromJson(Map<String, dynamic> json) => _$PropertyLeaseDtoFromJson(json);

@override final  String id;
@override final  String bookingId;
@override final  String tenantId;
@override final  String landlordId;
@override final  String content;
@override final  String status;
@override final  String? signedAt;
@override final  String? blockchainTxHash;
@override final  String onChainStatus;
@override final  String? contractAddress;
@override final  String? tokenId;
@override final  String createdAt;
@override final  String updatedAt;

/// Create a copy of PropertyLeaseDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$PropertyLeaseDtoCopyWith<_PropertyLeaseDto> get copyWith => __$PropertyLeaseDtoCopyWithImpl<_PropertyLeaseDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$PropertyLeaseDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _PropertyLeaseDto&&(identical(other.id, id) || other.id == id)&&(identical(other.bookingId, bookingId) || other.bookingId == bookingId)&&(identical(other.tenantId, tenantId) || other.tenantId == tenantId)&&(identical(other.landlordId, landlordId) || other.landlordId == landlordId)&&(identical(other.content, content) || other.content == content)&&(identical(other.status, status) || other.status == status)&&(identical(other.signedAt, signedAt) || other.signedAt == signedAt)&&(identical(other.blockchainTxHash, blockchainTxHash) || other.blockchainTxHash == blockchainTxHash)&&(identical(other.onChainStatus, onChainStatus) || other.onChainStatus == onChainStatus)&&(identical(other.contractAddress, contractAddress) || other.contractAddress == contractAddress)&&(identical(other.tokenId, tokenId) || other.tokenId == tokenId)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt)&&(identical(other.updatedAt, updatedAt) || other.updatedAt == updatedAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,bookingId,tenantId,landlordId,content,status,signedAt,blockchainTxHash,onChainStatus,contractAddress,tokenId,createdAt,updatedAt);

@override
String toString() {
  return 'PropertyLeaseDto(id: $id, bookingId: $bookingId, tenantId: $tenantId, landlordId: $landlordId, content: $content, status: $status, signedAt: $signedAt, blockchainTxHash: $blockchainTxHash, onChainStatus: $onChainStatus, contractAddress: $contractAddress, tokenId: $tokenId, createdAt: $createdAt, updatedAt: $updatedAt)';
}


}

/// @nodoc
abstract mixin class _$PropertyLeaseDtoCopyWith<$Res> implements $PropertyLeaseDtoCopyWith<$Res> {
  factory _$PropertyLeaseDtoCopyWith(_PropertyLeaseDto value, $Res Function(_PropertyLeaseDto) _then) = __$PropertyLeaseDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String bookingId, String tenantId, String landlordId, String content, String status, String? signedAt, String? blockchainTxHash, String onChainStatus, String? contractAddress, String? tokenId, String createdAt, String updatedAt
});




}
/// @nodoc
class __$PropertyLeaseDtoCopyWithImpl<$Res>
    implements _$PropertyLeaseDtoCopyWith<$Res> {
  __$PropertyLeaseDtoCopyWithImpl(this._self, this._then);

  final _PropertyLeaseDto _self;
  final $Res Function(_PropertyLeaseDto) _then;

/// Create a copy of PropertyLeaseDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? bookingId = null,Object? tenantId = null,Object? landlordId = null,Object? content = null,Object? status = null,Object? signedAt = freezed,Object? blockchainTxHash = freezed,Object? onChainStatus = null,Object? contractAddress = freezed,Object? tokenId = freezed,Object? createdAt = null,Object? updatedAt = null,}) {
  return _then(_PropertyLeaseDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,bookingId: null == bookingId ? _self.bookingId : bookingId // ignore: cast_nullable_to_non_nullable
as String,tenantId: null == tenantId ? _self.tenantId : tenantId // ignore: cast_nullable_to_non_nullable
as String,landlordId: null == landlordId ? _self.landlordId : landlordId // ignore: cast_nullable_to_non_nullable
as String,content: null == content ? _self.content : content // ignore: cast_nullable_to_non_nullable
as String,status: null == status ? _self.status : status // ignore: cast_nullable_to_non_nullable
as String,signedAt: freezed == signedAt ? _self.signedAt : signedAt // ignore: cast_nullable_to_non_nullable
as String?,blockchainTxHash: freezed == blockchainTxHash ? _self.blockchainTxHash : blockchainTxHash // ignore: cast_nullable_to_non_nullable
as String?,onChainStatus: null == onChainStatus ? _self.onChainStatus : onChainStatus // ignore: cast_nullable_to_non_nullable
as String,contractAddress: freezed == contractAddress ? _self.contractAddress : contractAddress // ignore: cast_nullable_to_non_nullable
as String?,tokenId: freezed == tokenId ? _self.tokenId : tokenId // ignore: cast_nullable_to_non_nullable
as String?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,updatedAt: null == updatedAt ? _self.updatedAt : updatedAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
