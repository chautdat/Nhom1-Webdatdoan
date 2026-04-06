import { ref, onMounted, onBeforeUnmount } from "vue";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

// Composable STOMP over SockJS với auto reconnect
// options: { headers, debug }
export function useWebSocket(url = "http://localhost:3000/ws", options = {}) {
  const isConnected = ref(false);
  let client = null;
  const { headers = {}, debug = false } = options;

  const connect = () => {
    client = new Client({
      webSocketFactory: () => new SockJS(url),
      debug: debug ? (msg) => console.log(msg) : () => {},
      reconnectDelay: 5000,
      connectHeaders: headers,
      onConnect: () => {
        console.log("✅ WebSocket connected");
        isConnected.value = true;
        // resubscribe handled by client via active subscriptions
        Object.values(client.subscriptions || {}).forEach((sub) => sub.unsubscribe());
        pendingSubscriptions.forEach((s) => actuallySubscribe(s.topic, s.cb));
      },
      onStompError: (frame) => {
        console.warn("⚠️ STOMP error", frame.headers["message"]);
      },
      onWebSocketClose: () => {
        isConnected.value = false;
      },
    });
    client.activate();
  };

  const pendingSubscriptions = [];

  const actuallySubscribe = (topic, cb) => {
    if (!client || !client.connected) return;
    return client.subscribe(topic, (msg) => {
      try {
        cb(JSON.parse(msg.body));
      } catch (e) {
        cb(msg.body);
      }
    });
  };

  const subscribe = (topic, cb) => {
    pendingSubscriptions.push({ topic, cb });
    return actuallySubscribe(topic, cb);
  };

  const send = (destination, message) => {
    if (client && client.connected) {
      client.publish({ destination, body: JSON.stringify(message) });
      console.log("➡️ Sent to", destination, message);
    }
  };

  const disconnect = () => {
    if (client) {
      client.deactivate();
    }
    isConnected.value = false;
  };

  onMounted(connect);
  onBeforeUnmount(disconnect);

  return { isConnected, subscribe, send, disconnect };
}
