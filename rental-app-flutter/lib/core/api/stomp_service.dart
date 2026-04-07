import 'dart:async';
import 'dart:convert';
import 'package:stomp_dart_client/stomp_dart_client.dart';

class StompService {
  static final StompService _instance = StompService._internal();
  factory StompService() => _instance;
  StompClient? _client;
  bool _connected = false;
  final _subscriptions = <String, dynamic>{};

  StompService._internal();

  bool get isConnected => _connected;

  Future<void> connect(String accessToken) async {
    if (_connected) return;

    final completer = Completer<void>();

    _client = StompClient(
      config: StompConfig(
        url: 'ws://localhost:8080/ws/websocket',
        onConnect: (frame) {
          _connected = true;
          if (!completer.isCompleted) completer.complete();
        },
        onStompError: (frame) {
          _connected = false;
          if (!completer.isCompleted) {
            completer.completeError('STOMP Error: ${frame.body}');
          }
        },
        onDisconnect: (frame) {
          _connected = false;
        },
        onWebSocketError: (error) {
          _connected = false;
          if (!completer.isCompleted) {
            completer.completeError('WebSocket Error: $error');
          }
        },
        stompConnectHeaders: {
          'Authorization': 'Bearer $accessToken',
        },
        webSocketConnectHeaders: {
          'Authorization': 'Bearer $accessToken',
        },
        reconnectDelay: const Duration(seconds: 5),
      ),
    );
    _client?.activate();

    // Wait for connection or timeout after 10s
    return completer.future.timeout(
      const Duration(seconds: 10),
      onTimeout: () {
        // Don't fail — WS is optional, HTTP fallback works
      },
    );
  }

  void subscribe(
    String destination,
    void Function(Map<String, dynamic>) callback,
  ) {
    if (!_connected || _client == null) return;

    // Unsubscribe from existing subscription on same destination
    unsubscribe(destination);

    final sub = _client!.subscribe(
      destination: destination,
      callback: (frame) {
        if (frame.body != null) {
          try {
            callback(json.decode(frame.body!));
          } catch (_) {}
        }
      },
    );
    _subscriptions[destination] = sub;
  }

  void unsubscribe(String destination) {
    final sub = _subscriptions.remove(destination);
    if (sub != null) {
      sub(unsubscribeHeaders: {});
    }
  }

  void sendMessage(String destination, Map<String, dynamic> body) {
    if (!_connected || _client == null) return;
    _client!.send(
      destination: destination,
      body: json.encode(body),
    );
  }

  void disconnect() {
    _subscriptions.clear();
    _client?.deactivate();
    _connected = false;
  }
}
