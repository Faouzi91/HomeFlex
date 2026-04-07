import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../providers/chat_provider.dart';
import '../models/message_dto.dart';
import '../../auth/providers/auth_provider.dart';
import '../../../core/api/stomp_service.dart';

class ChatRoomScreen extends ConsumerStatefulWidget {
  final String roomId;
  const ChatRoomScreen({super.key, required this.roomId});

  @override
  ConsumerState<ChatRoomScreen> createState() => _ChatRoomScreenState();
}

class _ChatRoomScreenState extends ConsumerState<ChatRoomScreen> {
  final _messageController = TextEditingController();
  final _scrollController = ScrollController();
  List<MessageDto> _messages = [];
  bool _isSending = false;
  final _stompService = StompService();

  @override
  void initState() {
    super.initState();
    _connectWebSocket();
  }

  @override
  void dispose() {
    _messageController.dispose();
    _scrollController.dispose();
    _stompService.unsubscribe('/topic/chat.${widget.roomId}');
    super.dispose();
  }

  Future<void> _connectWebSocket() async {
    try {
      // StompService should already be connected from app startup,
      // but attempt connection if not
      if (!_stompService.isConnected) {
        // Cookie-based auth — pass empty token; server reads session cookie
        await _stompService.connect('');
      }

      _stompService.subscribe(
        '/topic/chat.${widget.roomId}',
        (data) {
          if (!mounted) return;
          try {
            final newMessage = MessageDto.fromJson(data);
            setState(() {
              // Avoid duplicates
              if (!_messages.any((m) => m.id == newMessage.id)) {
                _messages.add(newMessage);
              }
            });
            _scrollToBottom();
          } catch (_) {}
        },
      );
    } catch (_) {
      // WebSocket is optional — HTTP polling is the fallback
    }
  }

  void _scrollToBottom() {
    if (_scrollController.hasClients) {
      Future.delayed(const Duration(milliseconds: 100), () {
        if (_scrollController.hasClients) {
          _scrollController.animateTo(
            _scrollController.position.maxScrollExtent,
            duration: const Duration(milliseconds: 300),
            curve: Curves.easeOut,
          );
        }
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final messagesAsync = ref.watch(chatMessagesProvider(widget.roomId));
    final currentUser = ref.watch(authProvider).user;

    return Scaffold(
      appBar: AppBar(title: const Text('Chat')),
      body: Column(
        children: [
          Expanded(
            child: messagesAsync.when(
              data: (messages) {
                // Merge HTTP messages with any WS messages not yet in the list
                final httpIds = messages.map((m) => m.id).toSet();
                final extraWs = _messages.where((m) => !httpIds.contains(m.id));
                _messages = [...messages, ...extraWs];

                WidgetsBinding.instance.addPostFrameCallback((_) => _scrollToBottom());
                if (_messages.isEmpty) {
                  return const Center(
                    child: Text('No messages yet. Start the conversation!',
                        style: TextStyle(color: Colors.grey)),
                  );
                }
                return ListView.builder(
                  controller: _scrollController,
                  padding: const EdgeInsets.all(16),
                  itemCount: _messages.length,
                  itemBuilder: (context, index) {
                    final msg = _messages[index];
                    final isMe = msg.senderId == currentUser?.id;
                    return _MessageBubble(message: msg, isMe: isMe);
                  },
                );
              },
              loading: () => const Center(child: CircularProgressIndicator()),
              error: (err, stack) => Center(child: Text('Error: $err')),
            ),
          ),
          _buildInputBar(),
        ],
      ),
    );
  }

  Widget _buildInputBar() {
    return Container(
      padding: const EdgeInsets.all(8),
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surface,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.05),
            blurRadius: 4,
            offset: const Offset(0, -2),
          ),
        ],
      ),
      child: SafeArea(
        child: Row(
          children: [
            Expanded(
              child: TextField(
                controller: _messageController,
                decoration: InputDecoration(
                  hintText: 'Type a message...',
                  border: OutlineInputBorder(borderRadius: BorderRadius.circular(24)),
                  contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                ),
                textInputAction: TextInputAction.send,
                onSubmitted: (_) => _sendMessage(),
              ),
            ),
            const SizedBox(width: 8),
            IconButton.filled(
              onPressed: _isSending ? null : _sendMessage,
              icon: _isSending
                  ? const SizedBox(
                      width: 20,
                      height: 20,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    )
                  : const Icon(Icons.send),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _sendMessage() async {
    final text = _messageController.text.trim();
    if (text.isEmpty) return;

    setState(() => _isSending = true);
    _messageController.clear();

    try {
      await ref.read(chatNotifierProvider).sendMessage(widget.roomId, text);
      // The server will broadcast via WebSocket, but also refresh HTTP as fallback
      ref.invalidate(chatMessagesProvider(widget.roomId));
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to send: $e')),
        );
      }
    } finally {
      if (mounted) setState(() => _isSending = false);
    }
  }
}

class _MessageBubble extends StatelessWidget {
  final MessageDto message;
  final bool isMe;
  const _MessageBubble({required this.message, required this.isMe});

  @override
  Widget build(BuildContext context) {
    return Align(
      alignment: isMe ? Alignment.centerRight : Alignment.centerLeft,
      child: Container(
        constraints: BoxConstraints(maxWidth: MediaQuery.of(context).size.width * 0.75),
        margin: const EdgeInsets.symmetric(vertical: 4),
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
        decoration: BoxDecoration(
          color: isMe
              ? Theme.of(context).colorScheme.primary
              : Theme.of(context).colorScheme.surfaceContainerHighest,
          borderRadius: BorderRadius.only(
            topLeft: const Radius.circular(16),
            topRight: const Radius.circular(16),
            bottomLeft: Radius.circular(isMe ? 16 : 4),
            bottomRight: Radius.circular(isMe ? 4 : 16),
          ),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (!isMe)
              Padding(
                padding: const EdgeInsets.only(bottom: 4),
                child: Text(
                  message.senderName,
                  style: TextStyle(
                    fontSize: 12,
                    fontWeight: FontWeight.bold,
                    color: isMe ? Colors.white70 : Colors.grey,
                  ),
                ),
              ),
            Text(
              message.messageText,
              style: TextStyle(color: isMe ? Colors.white : null),
            ),
            const SizedBox(height: 4),
            Text(
              message.createdAt.length >= 16 ? message.createdAt.substring(11, 16) : '',
              style: TextStyle(
                fontSize: 10,
                color: isMe ? Colors.white60 : Colors.grey,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
