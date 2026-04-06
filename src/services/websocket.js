import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

class WebSocketService {
  constructor() {
    this.client = null;
    this.connected = false;
    this.subscriptions = {};
  }

  connect(token, userEmail) {
    return new Promise((resolve, reject) => {
      const socket = new SockJS("http://localhost:3000/ws");

      this.client = new Client({
        webSocketFactory: () => socket,
        connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
        debug: (str) => console.log("🔌 WebSocket:", str),
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
      });

      this.client.onConnect = () => {
        console.log("✅ WebSocket Connected");
        this.connected = true;
        if (userEmail) this.subscribeToUserOrders(userEmail);
        resolve();
      };

      this.client.onStompError = (frame) => {
        console.error("❌ WebSocket Error:", frame);
        this.connected = false;
        reject(frame);
      };

      this.client.activate();
    });
  }

  subscribeToUserOrders(userEmail, callback) {
    if (!this.client || !this.connected) return;

    const subscription = this.client.subscribe(
      `/user/${userEmail}/queue/orders`,
      (message) => {
        const data = JSON.parse(message.body);
        console.log("📦 Order Update:", data);
        if (callback) callback(data);
        window.dispatchEvent(new CustomEvent("order-updated", { detail: data }));
      }
    );

    this.subscriptions.userOrders = subscription;
  }

  disconnect() {
    if (this.client) {
      Object.values(this.subscriptions).forEach((sub) => sub?.unsubscribe());
      this.client.deactivate();
      this.connected = false;
      this.subscriptions = {};
      console.log("🔌 WebSocket Disconnected");
    }
  }
}

export default new WebSocketService();
