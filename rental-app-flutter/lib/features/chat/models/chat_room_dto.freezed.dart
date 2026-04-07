// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'chat_room_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$ChatRoomDto {

 String get id; String? get propertyId; String? get propertyTitle; UserDto get tenant; UserDto get landlord; String? get lastMessageAt; int? get unreadCount;
/// Create a copy of ChatRoomDto
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$ChatRoomDtoCopyWith<ChatRoomDto> get copyWith => _$ChatRoomDtoCopyWithImpl<ChatRoomDto>(this as ChatRoomDto, _$identity);

  /// Serializes this ChatRoomDto to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is ChatRoomDto&&(identical(other.id, id) || other.id == id)&&(identical(other.propertyId, propertyId) || other.propertyId == propertyId)&&(identical(other.propertyTitle, propertyTitle) || other.propertyTitle == propertyTitle)&&(identical(other.tenant, tenant) || other.tenant == tenant)&&(identical(other.landlord, landlord) || other.landlord == landlord)&&(identical(other.lastMessageAt, lastMessageAt) || other.lastMessageAt == lastMessageAt)&&(identical(other.unreadCount, unreadCount) || other.unreadCount == unreadCount));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,propertyId,propertyTitle,tenant,landlord,lastMessageAt,unreadCount);

@override
String toString() {
  return 'ChatRoomDto(id: $id, propertyId: $propertyId, propertyTitle: $propertyTitle, tenant: $tenant, landlord: $landlord, lastMessageAt: $lastMessageAt, unreadCount: $unreadCount)';
}


}

/// @nodoc
abstract mixin class $ChatRoomDtoCopyWith<$Res>  {
  factory $ChatRoomDtoCopyWith(ChatRoomDto value, $Res Function(ChatRoomDto) _then) = _$ChatRoomDtoCopyWithImpl;
@useResult
$Res call({
 String id, String? propertyId, String? propertyTitle, UserDto tenant, UserDto landlord, String? lastMessageAt, int? unreadCount
});


$UserDtoCopyWith<$Res> get tenant;$UserDtoCopyWith<$Res> get landlord;

}
/// @nodoc
class _$ChatRoomDtoCopyWithImpl<$Res>
    implements $ChatRoomDtoCopyWith<$Res> {
  _$ChatRoomDtoCopyWithImpl(this._self, this._then);

  final ChatRoomDto _self;
  final $Res Function(ChatRoomDto) _then;

/// Create a copy of ChatRoomDto
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? propertyId = freezed,Object? propertyTitle = freezed,Object? tenant = null,Object? landlord = null,Object? lastMessageAt = freezed,Object? unreadCount = freezed,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,propertyId: freezed == propertyId ? _self.propertyId : propertyId // ignore: cast_nullable_to_non_nullable
as String?,propertyTitle: freezed == propertyTitle ? _self.propertyTitle : propertyTitle // ignore: cast_nullable_to_non_nullable
as String?,tenant: null == tenant ? _self.tenant : tenant // ignore: cast_nullable_to_non_nullable
as UserDto,landlord: null == landlord ? _self.landlord : landlord // ignore: cast_nullable_to_non_nullable
as UserDto,lastMessageAt: freezed == lastMessageAt ? _self.lastMessageAt : lastMessageAt // ignore: cast_nullable_to_non_nullable
as String?,unreadCount: freezed == unreadCount ? _self.unreadCount : unreadCount // ignore: cast_nullable_to_non_nullable
as int?,
  ));
}
/// Create a copy of ChatRoomDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res> get tenant {
  
  return $UserDtoCopyWith<$Res>(_self.tenant, (value) {
    return _then(_self.copyWith(tenant: value));
  });
}/// Create a copy of ChatRoomDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res> get landlord {
  
  return $UserDtoCopyWith<$Res>(_self.landlord, (value) {
    return _then(_self.copyWith(landlord: value));
  });
}
}


/// Adds pattern-matching-related methods to [ChatRoomDto].
extension ChatRoomDtoPatterns on ChatRoomDto {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _ChatRoomDto value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _ChatRoomDto() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _ChatRoomDto value)  $default,){
final _that = this;
switch (_that) {
case _ChatRoomDto():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _ChatRoomDto value)?  $default,){
final _that = this;
switch (_that) {
case _ChatRoomDto() when $default != null:
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

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( String id,  String? propertyId,  String? propertyTitle,  UserDto tenant,  UserDto landlord,  String? lastMessageAt,  int? unreadCount)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _ChatRoomDto() when $default != null:
return $default(_that.id,_that.propertyId,_that.propertyTitle,_that.tenant,_that.landlord,_that.lastMessageAt,_that.unreadCount);case _:
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

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( String id,  String? propertyId,  String? propertyTitle,  UserDto tenant,  UserDto landlord,  String? lastMessageAt,  int? unreadCount)  $default,) {final _that = this;
switch (_that) {
case _ChatRoomDto():
return $default(_that.id,_that.propertyId,_that.propertyTitle,_that.tenant,_that.landlord,_that.lastMessageAt,_that.unreadCount);case _:
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

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( String id,  String? propertyId,  String? propertyTitle,  UserDto tenant,  UserDto landlord,  String? lastMessageAt,  int? unreadCount)?  $default,) {final _that = this;
switch (_that) {
case _ChatRoomDto() when $default != null:
return $default(_that.id,_that.propertyId,_that.propertyTitle,_that.tenant,_that.landlord,_that.lastMessageAt,_that.unreadCount);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _ChatRoomDto implements ChatRoomDto {
  const _ChatRoomDto({required this.id, this.propertyId, this.propertyTitle, required this.tenant, required this.landlord, this.lastMessageAt, this.unreadCount});
  factory _ChatRoomDto.fromJson(Map<String, dynamic> json) => _$ChatRoomDtoFromJson(json);

@override final  String id;
@override final  String? propertyId;
@override final  String? propertyTitle;
@override final  UserDto tenant;
@override final  UserDto landlord;
@override final  String? lastMessageAt;
@override final  int? unreadCount;

/// Create a copy of ChatRoomDto
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$ChatRoomDtoCopyWith<_ChatRoomDto> get copyWith => __$ChatRoomDtoCopyWithImpl<_ChatRoomDto>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$ChatRoomDtoToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _ChatRoomDto&&(identical(other.id, id) || other.id == id)&&(identical(other.propertyId, propertyId) || other.propertyId == propertyId)&&(identical(other.propertyTitle, propertyTitle) || other.propertyTitle == propertyTitle)&&(identical(other.tenant, tenant) || other.tenant == tenant)&&(identical(other.landlord, landlord) || other.landlord == landlord)&&(identical(other.lastMessageAt, lastMessageAt) || other.lastMessageAt == lastMessageAt)&&(identical(other.unreadCount, unreadCount) || other.unreadCount == unreadCount));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,propertyId,propertyTitle,tenant,landlord,lastMessageAt,unreadCount);

@override
String toString() {
  return 'ChatRoomDto(id: $id, propertyId: $propertyId, propertyTitle: $propertyTitle, tenant: $tenant, landlord: $landlord, lastMessageAt: $lastMessageAt, unreadCount: $unreadCount)';
}


}

/// @nodoc
abstract mixin class _$ChatRoomDtoCopyWith<$Res> implements $ChatRoomDtoCopyWith<$Res> {
  factory _$ChatRoomDtoCopyWith(_ChatRoomDto value, $Res Function(_ChatRoomDto) _then) = __$ChatRoomDtoCopyWithImpl;
@override @useResult
$Res call({
 String id, String? propertyId, String? propertyTitle, UserDto tenant, UserDto landlord, String? lastMessageAt, int? unreadCount
});


@override $UserDtoCopyWith<$Res> get tenant;@override $UserDtoCopyWith<$Res> get landlord;

}
/// @nodoc
class __$ChatRoomDtoCopyWithImpl<$Res>
    implements _$ChatRoomDtoCopyWith<$Res> {
  __$ChatRoomDtoCopyWithImpl(this._self, this._then);

  final _ChatRoomDto _self;
  final $Res Function(_ChatRoomDto) _then;

/// Create a copy of ChatRoomDto
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? propertyId = freezed,Object? propertyTitle = freezed,Object? tenant = null,Object? landlord = null,Object? lastMessageAt = freezed,Object? unreadCount = freezed,}) {
  return _then(_ChatRoomDto(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as String,propertyId: freezed == propertyId ? _self.propertyId : propertyId // ignore: cast_nullable_to_non_nullable
as String?,propertyTitle: freezed == propertyTitle ? _self.propertyTitle : propertyTitle // ignore: cast_nullable_to_non_nullable
as String?,tenant: null == tenant ? _self.tenant : tenant // ignore: cast_nullable_to_non_nullable
as UserDto,landlord: null == landlord ? _self.landlord : landlord // ignore: cast_nullable_to_non_nullable
as UserDto,lastMessageAt: freezed == lastMessageAt ? _self.lastMessageAt : lastMessageAt // ignore: cast_nullable_to_non_nullable
as String?,unreadCount: freezed == unreadCount ? _self.unreadCount : unreadCount // ignore: cast_nullable_to_non_nullable
as int?,
  ));
}

/// Create a copy of ChatRoomDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res> get tenant {
  
  return $UserDtoCopyWith<$Res>(_self.tenant, (value) {
    return _then(_self.copyWith(tenant: value));
  });
}/// Create a copy of ChatRoomDto
/// with the given fields replaced by the non-null parameter values.
@override
@pragma('vm:prefer-inline')
$UserDtoCopyWith<$Res> get landlord {
  
  return $UserDtoCopyWith<$Res>(_self.landlord, (value) {
    return _then(_self.copyWith(landlord: value));
  });
}
}

// dart format on
