// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'review_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ReviewDto {

 String get id; ReviewType get type; String? get propertyId; UserDto? get targetUser; UserDto? get reviewer; int get rating; String? get comment; String get createdAt;
/// Create a copy of ReviewDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ReviewDtoCopyWith<ReviewDto> get copyWith => _$ReviewDtoCopyWithImpl<ReviewDto>(this as ReviewDto, _$identity);

  /// Serializes this ReviewDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ReviewDto&&(identical(other.id, id) || other.id == id)&&(identical(other.type, type) || other.type == type)&&(identical(other.propertyId, propertyId) || other.propertyId == propertyId)&&(identical(other.targetUser, targetUser) || other.targetUser == targetUser)&&(identical(other.reviewer, reviewer) || other.reviewer == reviewer)&&(identical(other.rating, rating) || other.rating == rating)&&(identical(other.comment, comment) || other.comment == comment)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,type,propertyId,targetUser,reviewer,rating,comment,createdAt);

@override
String toString() {
  return 'ReviewDto(id: $id, type: $type, propertyId: $propertyId, targetUser: $targetUser, reviewer: $reviewer, rating: $rating, comment: $comment, createdAt: $createdAt)';
}


}

/// @nodoc
abstract mixin class $ReviewDtoCopyWith<$Res>  {
  factory $ReviewDtoCopyWith(ReviewDto value, $Res Function(ReviewDto) _then) = _$ReviewDtoCopyWithImpl;
@useResult
$Res call({
 String id, ReviewType type, String? propertyId, UserDto? targetUser, UserDto? reviewer, int rating, String? comment, String createdAt
});


$UserDtoCopyWith<$Res>? get targetUser;$UserDtoCopyWith<$Res>? get reviewer;

}
/// @nodoc
class _$ReviewDtoCopyWithImpl<$Res>
    implements $ReviewDtoCopyWith<$Res> {
  _$ReviewDtoCopyWithImpl(this._self, this._then);

  final ReviewDto _self;
  final $Res Function(ReviewDto) _then;

/// Create a copy of ReviewDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? type = null,Object? propertyId = freezed,Object? targetUser = freezed,Object? reviewer = freezed,Object? rating = null,Object? comment = freezed,Object? createdAt = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as ReviewType,propertyId: freezed == propertyId ? _self.propertyId : propertyId // ignore: cast_nullable_to_non_nullable
as String?,targetUser: freezed == targetUser ? _self.targetUser : targetUser // ignore: cast_nullable_to_non_nullable
as UserDto?,reviewer: freezed == reviewer ? _self.reviewer : reviewer // ignore: cast_nullable_to_non_nullable
as UserDto?,rating: null == rating ? _self.rating : rating // ignore: cast_nullable_to_non_nullable
as int,comment: freezed == comment ? _self.comment : comment // ignore: cast_nullable_to_non_nullable
as String?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}
/// Create a copy of ReviewDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res>? get targetUser {
    if (_self.targetUser == null) {
    return null;
  }

  return $UserDtoCopyWith<$Res>(_self.targetUser!, (value) {
    return _then(_self.copyWith(targetUser: value));
  });
}/// Create a copy of ReviewDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res>? get reviewer {
    if (_self.reviewer == null) {
    return null;
  }

  return $UserDtoCopyWith<$Res>(_self.reviewer!, (value) {
    return _then(_self.copyWith(reviewer: value));
  });
}
}


/// Adds pattern-matching-related methods to [ReviewDto].
extension ReviewDtoPatterns on ReviewDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ReviewDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ReviewDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ReviewDto value)  $default,){
final _that = this;
switch (_that) {
case _ReviewDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ReviewDto value)?  $default,){
final _that = this;
switch (_that) {
case _ReviewDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  ReviewType type,  String? propertyId,  UserDto? targetUser,  UserDto? reviewer,  int rating,  String? comment,  String createdAt)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ReviewDto() when $default != null:
return $default(_that.id,_that.type,_that.propertyId,_that.targetUser,_that.reviewer,_that.rating,_that.comment,_that.createdAt);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  ReviewType type,  String? propertyId,  UserDto? targetUser,  UserDto? reviewer,  int rating,  String? comment,  String createdAt)  $default,) {final _that = this;
switch (_that) {
case _ReviewDto():
return $default(_that.id,_that.type,_that.propertyId,_that.targetUser,_that.reviewer,_that.rating,_that.comment,_that.createdAt);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  ReviewType type,  String? propertyId,  UserDto? targetUser,  UserDto? reviewer,  int rating,  String? comment,  String createdAt)?  $default,) {final _that = this;
switch (_that) {
case _ReviewDto() when $default != null:
return $default(_that.id,_that.type,_that.propertyId,_that.targetUser,_that.reviewer,_that.rating,_that.comment,_that.createdAt);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ReviewDto implements ReviewDto {
  const _ReviewDto({required this.id, required this.type, this.propertyId, this.targetUser, this.reviewer, required this.rating, this.comment, required this.createdAt});
  factory _ReviewDto.fromJson(Map<String, dynamic> json) => _$ReviewDtoFromJson(json);

@override final  String id;
@override final  ReviewType type;
@override final  String? propertyId;
@override final  UserDto? targetUser;
@override final  UserDto? reviewer;
@override final  int rating;
@override final  String? comment;
@override final  String createdAt;

/// Create a copy of ReviewDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ReviewDtoCopyWith<_ReviewDto> get copyWith => __$ReviewDtoCopyWithImpl<_ReviewDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ReviewDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _ReviewDto&&(identical(other.id, id) || other.id == id)&&(identical(other.type, type) || other.type == type)&&(identical(other.propertyId, propertyId) || other.propertyId == propertyId)&&(identical(other.targetUser, targetUser) || other.targetUser == targetUser)&&(identical(other.reviewer, reviewer) || other.reviewer == reviewer)&&(identical(other.rating, rating) || other.rating == rating)&&(identical(other.comment, comment) || other.comment == comment)&&(identical(other.createdAt, createdAt) || other.createdAt == createdAt));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,type,propertyId,targetUser,reviewer,rating,comment,createdAt);

@override
String toString() {
  return 'ReviewDto(id: $id, type: $type, propertyId: $propertyId, targetUser: $targetUser, reviewer: $reviewer, rating: $rating, comment: $comment, createdAt: $createdAt)';
}


}

/// @nodoc
abstract mixin class _$ReviewDtoCopyWith<$Res> implements $ReviewDtoCopyWith<$Res> {
  factory _$ReviewDtoCopyWith(_ReviewDto value, $Res Function(_ReviewDto) _then) = __$ReviewDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, ReviewType type, String? propertyId, UserDto? targetUser, UserDto? reviewer, int rating, String? comment, String createdAt
});


@override $UserDtoCopyWith<$Res>? get targetUser;@override $UserDtoCopyWith<$Res>? get reviewer;

}
/// @nodoc
class __$ReviewDtoCopyWithImpl<$Res>
    implements _$ReviewDtoCopyWith<$Res> {
  __$ReviewDtoCopyWithImpl(this._self, this._then);

  final _ReviewDto _self;
  final $Res Function(_ReviewDto) _then;

/// Create a copy of ReviewDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? type = null,Object? propertyId = freezed,Object? targetUser = freezed,Object? reviewer = freezed,Object? rating = null,Object? comment = freezed,Object? createdAt = null,}) {
  return _then(_ReviewDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,type: null == type ? _self.type : type // ignore: cast_nullable_to_non_nullable
as ReviewType,propertyId: freezed == propertyId ? _self.propertyId : propertyId // ignore: cast_nullable_to_non_nullable
as String?,targetUser: freezed == targetUser ? _self.targetUser : targetUser // ignore: cast_nullable_to_non_nullable
as UserDto?,reviewer: freezed == reviewer ? _self.reviewer : reviewer // ignore: cast_nullable_to_non_nullable
as UserDto?,rating: null == rating ? _self.rating : rating // ignore: cast_nullable_to_non_nullable
as int,comment: freezed == comment ? _self.comment : comment // ignore: cast_nullable_to_non_nullable
as String?,createdAt: null == createdAt ? _self.createdAt : createdAt // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

/// Create a copy of ReviewDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res>? get targetUser {
    if (_self.targetUser == null) {
    return null;
  }

  return $UserDtoCopyWith<$Res>(_self.targetUser!, (value) {
    return _then(_self.copyWith(targetUser: value));
  });
}/// Create a copy of ReviewDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res>? get reviewer {
    if (_self.reviewer == null) {
    return null;
  }

  return $UserDtoCopyWith<$Res>(_self.reviewer!, (value) {
    return _then(_self.copyWith(reviewer: value));
  });
}
}


/// @nodoc
mixin _$ReviewCreateRequest {

 String? get propertyId; String? get targetUserId; int get rating; String? get comment;
/// Create a copy of ReviewCreateRequest
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ReviewCreateRequestCopyWith<ReviewCreateRequest> get copyWith => _$ReviewCreateRequestCopyWithImpl<ReviewCreateRequest>(this as ReviewCreateRequest, _$identity);

  /// Serializes this ReviewCreateRequest to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ReviewCreateRequest&&(identical(other.propertyId, propertyId) || other.propertyId == propertyId)&&(identical(other.targetUserId, targetUserId) || other.targetUserId == targetUserId)&&(identical(other.rating, rating) || other.rating == rating)&&(identical(other.comment, comment) || other.comment == comment));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,propertyId,targetUserId,rating,comment);

@override
String toString() {
  return 'ReviewCreateRequest(propertyId: $propertyId, targetUserId: $targetUserId, rating: $rating, comment: $comment)';
}


}

/// @nodoc
abstract mixin class $ReviewCreateRequestCopyWith<$Res>  {
  factory $ReviewCreateRequestCopyWith(ReviewCreateRequest value, $Res Function(ReviewCreateRequest) _then) = _$ReviewCreateRequestCopyWithImpl;
@useResult
$Res call({
 String? propertyId, String? targetUserId, int rating, String? comment
});




}
/// @nodoc
class _$ReviewCreateRequestCopyWithImpl<$Res>
    implements $ReviewCreateRequestCopyWith<$Res> {
  _$ReviewCreateRequestCopyWithImpl(this._self, this._then);

  final ReviewCreateRequest _self;
  final $Res Function(ReviewCreateRequest) _then;

/// Create a copy of ReviewCreateRequest
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? propertyId = freezed,Object? targetUserId = freezed,Object? rating = null,Object? comment = freezed,}) {
  return _then(_self.copyWith(
propertyId: freezed == propertyId ? _self.propertyId : propertyId // ignore: cast_nullable_to_non_nullable
as String?,targetUserId: freezed == targetUserId ? _self.targetUserId : targetUserId // ignore: cast_nullable_to_non_nullable
as String?,rating: null == rating ? _self.rating : rating // ignore: cast_nullable_to_non_nullable
as int,comment: freezed == comment ? _self.comment : comment // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}

}


/// Adds pattern-matching-related methods to [ReviewCreateRequest].
extension ReviewCreateRequestPatterns on ReviewCreateRequest {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ReviewCreateRequest value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ReviewCreateRequest() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ReviewCreateRequest value)  $default,){
final _that = this;
switch (_that) {
case _ReviewCreateRequest():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ReviewCreateRequest value)?  $default,){
final _that = this;
switch (_that) {
case _ReviewCreateRequest() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String? propertyId,  String? targetUserId,  int rating,  String? comment)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ReviewCreateRequest() when $default != null:
return $default(_that.propertyId,_that.targetUserId,_that.rating,_that.comment);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String? propertyId,  String? targetUserId,  int rating,  String? comment)  $default,) {final _that = this;
switch (_that) {
case _ReviewCreateRequest():
return $default(_that.propertyId,_that.targetUserId,_that.rating,_that.comment);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String? propertyId,  String? targetUserId,  int rating,  String? comment)?  $default,) {final _that = this;
switch (_that) {
case _ReviewCreateRequest() when $default != null:
return $default(_that.propertyId,_that.targetUserId,_that.rating,_that.comment);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ReviewCreateRequest implements ReviewCreateRequest {
  const _ReviewCreateRequest({this.propertyId, this.targetUserId, required this.rating, this.comment});
  factory _ReviewCreateRequest.fromJson(Map<String, dynamic> json) => _$ReviewCreateRequestFromJson(json);

@override final  String? propertyId;
@override final  String? targetUserId;
@override final  int rating;
@override final  String? comment;

/// Create a copy of ReviewCreateRequest
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ReviewCreateRequestCopyWith<_ReviewCreateRequest> get copyWith => __$ReviewCreateRequestCopyWithImpl<_ReviewCreateRequest>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ReviewCreateRequestToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _ReviewCreateRequest&&(identical(other.propertyId, propertyId) || other.propertyId == propertyId)&&(identical(other.targetUserId, targetUserId) || other.targetUserId == targetUserId)&&(identical(other.rating, rating) || other.rating == rating)&&(identical(other.comment, comment) || other.comment == comment));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,propertyId,targetUserId,rating,comment);

@override
String toString() {
  return 'ReviewCreateRequest(propertyId: $propertyId, targetUserId: $targetUserId, rating: $rating, comment: $comment)';
}


}

/// @nodoc
abstract mixin class _$ReviewCreateRequestCopyWith<$Res> implements $ReviewCreateRequestCopyWith<$Res> {
  factory _$ReviewCreateRequestCopyWith(_ReviewCreateRequest value, $Res Function(_ReviewCreateRequest) _then) = __$ReviewCreateRequestCopyWithImpl;
@override @useResult
$Res call({
 String? propertyId, String? targetUserId, int rating, String? comment
});




}
/// @nodoc
class __$ReviewCreateRequestCopyWithImpl<$Res>
    implements _$ReviewCreateRequestCopyWith<$Res> {
  __$ReviewCreateRequestCopyWithImpl(this._self, this._then);

  final _ReviewCreateRequest _self;
  final $Res Function(_ReviewCreateRequest) _then;

/// Create a copy of ReviewCreateRequest
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? propertyId = freezed,Object? targetUserId = freezed,Object? rating = null,Object? comment = freezed,}) {
  return _then(_ReviewCreateRequest(
propertyId: freezed == propertyId ? _self.propertyId : propertyId // ignore: cast_nullable_to_non_nullable
as String?,targetUserId: freezed == targetUserId ? _self.targetUserId : targetUserId // ignore: cast_nullable_to_non_nullable
as String?,rating: null == rating ? _self.rating : rating // ignore: cast_nullable_to_non_nullable
as int,comment: freezed == comment ? _self.comment : comment // ignore: cast_nullable_to_non_nullable
as String?,
  ));
}


}

// dart format on
