class AuctionWebSocketClient {
    constructor(auctionId, userId, options = {}) {
        this.auctionId = auctionId;
        this.userId = userId;
        this.socket = null;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = options.maxReconnectAttempts || 5;
        this.reconnectDelay = options.reconnectDelay || 3000;
        this.heartbeatInterval = options.heartbeatInterval || 30000;
        this.heartbeatTimer = null;

        this.onBidUpdate = options.onBidUpdate || this.defaultBidUpdate.bind(this);
        this.onConnectionChange = options.onConnectionChange || this.defaultConnectionChange.bind(this);
        this.onError = options.onError || this.defaultError.bind(this);

        this.init();
    }

    init() {
        this.connect();
    }

    connect() {
        if (this.socket) {
            this.disconnect();
        }

        try {
            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            const host = window.location.host;
            const contextPath = this.getContextPath();
            const wsUrl = `${protocol}//${host}${contextPath}/websocket/auction/${this.auctionId}`;

            console.log('Connecting to WebSocket:', wsUrl);
            this.onConnectionChange('connecting');

            this.socket = new WebSocket(wsUrl);
            this.setupEventHandlers();

        } catch (error) {
            console.error('Error creating WebSocket connection:', error);
            this.onError('Connection failed', error);
            this.onConnectionChange('error');
        }
    }

    setupEventHandlers() {
        this.socket.onopen = (event) => {
            console.log('WebSocket connected');
            this.onConnectionChange('connected');
            this.reconnectAttempts = 0;
            this.startHeartbeat();
        };

        this.socket.onmessage = (event) => {
            console.log('Received WebSocket message:', event.data);
            try {
                const data = JSON.parse(event.data);
                this.handleMessage(data);
            } catch (e) {
                console.error('Error parsing WebSocket message:', e);
                this.onError('Message parse error', e);
            }
        };

        this.socket.onclose = (event) => {
            console.log('WebSocket connection closed:', event.code, event.reason);
            this.onConnectionChange('disconnected');
            this.stopHeartbeat();

            if (event.code !== 1000 && this.reconnectAttempts < this.maxReconnectAttempts) {
                this.attemptReconnect();
            }
        };

        this.socket.onerror = (error) => {
            console.error('WebSocket error:', error);
            this.onError('WebSocket error', error);
        };
    }

    handleMessage(data) {
        switch (data.type) {
            case 'bidUpdate':
                if (data.auctionId === this.auctionId) {
                    this.onBidUpdate(data);
                }
                break;
            case 'auctionEnd':
                this.onAuctionEnd(data);
                break;
            case 'ping':
                this.sendPong();
                break;
            default:
                console.log('Unknown message type:', data.type);
        }
    }

    attemptReconnect() {
        this.reconnectAttempts++;
        const delay = this.reconnectDelay * this.reconnectAttempts;

        this.onConnectionChange('reconnecting', {
            attempt: this.reconnectAttempts,
            maxAttempts: this.maxReconnectAttempts,
            delay: delay
        });

        setTimeout(() => {
            this.connect();
        }, delay);
    }

    startHeartbeat() {
        this.stopHeartbeat();
        this.heartbeatTimer = setInterval(() => {
            if (this.socket && this.socket.readyState === WebSocket.OPEN) {
                this.sendPing();
            }
        }, this.heartbeatInterval);
    }

    stopHeartbeat() {
        if (this.heartbeatTimer) {
            clearInterval(this.heartbeatTimer);
            this.heartbeatTimer = null;
        }
    }

    sendPing() {
        this.send({type: 'ping', timestamp: Date.now()});
    }

    sendPong() {
        this.send({type: 'pong', timestamp: Date.now()});
    }

    send(data) {
        if (this.socket && this.socket.readyState === WebSocket.OPEN) {
            this.socket.send(JSON.stringify(data));
        }
    }

    disconnect() {
        this.stopHeartbeat();
        if (this.socket) {
            this.socket.close(1000, 'Client disconnect');
            this.socket = null;
        }
    }

    // Get context path from a global variable
    getContextPath() {
        if (window.contextPath) {
            return window.contextPath;
        }
        const path = window.location.pathname;
        const segments = path.split('/');
        return segments.length > 1 ? '/' + segments[1] : '';
    }

    defaultBidUpdate(data) {
        const currentBidElement = document.getElementById('currentBid');
        if (currentBidElement) {
            currentBidElement.textContent = data.amount.toFixed(2);
            this.animateElement(currentBidElement);
        }

        const amountInput = document.getElementById('amount');
        if (amountInput) {
            amountInput.min = (data.amount + 0.01).toFixed(2);
        }

        this.updateBidStatus(data);
    }

    updateBidStatus(data) {
        const bidStatusElement = document.getElementById('bidStatus');
        if (!bidStatusElement) return;

        if (data.userId === this.userId) {
            bidStatusElement.textContent = 'Your Bid';
            bidStatusElement.className = 'bid-status your-bid';
            bidStatusElement.style.display = 'block';
        } else {
            bidStatusElement.textContent = 'Outbid!';
            bidStatusElement.className = 'bid-status other-bid';
            bidStatusElement.style.display = 'block';

            // Hide after 5 seconds
            setTimeout(() => {
                bidStatusElement.style.display = 'none';
            }, 5000);
        }
    }

    animateElement(element) {
        element.style.transition = 'transform 0.3s ease';
        element.style.transform = 'scale(1.05)';
        setTimeout(() => {
            element.style.transform = 'scale(1)';
        }, 300);
    }

    defaultConnectionChange(status, data = {}) {
        const statusElement = document.getElementById('connectionStatus');
        if (!statusElement) return;

        let statusText = '';
        let className = 'status-indicator';

        switch (status) {
            case 'connecting':
                statusText = 'Connecting...';
                className += ' status-connecting';
                break;
            case 'connected':
                statusText = 'Connected';
                className += ' status-connected';
                break;
            case 'disconnected':
                statusText = 'Disconnected';
                className += ' status-disconnected';
                break;
            case 'reconnecting':
                statusText = `Reconnecting... (${data.attempt}/${data.maxAttempts})`;
                className += ' status-connecting';
                break;
            case 'error':
                statusText = 'Connection Error';
                className += ' status-disconnected';
                break;
        }

        statusElement.textContent = statusText;
        statusElement.className = className;
    }

    defaultError(message, error) {
        console.error('AuctionWebSocketClient error:', message, error);
    }

    onAuctionEnd(data) {
        console.log('Auction ended:', data);
        // Handle auction ending
        const endMessage = document.createElement('div');
        endMessage.className = 'auction-ended-message';
        endMessage.innerHTML = `
            <h3>Auction Ended!</h3>
            <p>Final bid: ${data.finalBid || 'N/A'}</p>
        `;
        document.body.appendChild(endMessage);
    }
}

// Initialize WebSocket
function initAuctionWebSocket(auctionId, userId, options = {}) {
    // return a new, if does not exist
    if (window.auctionWebSocket && window.auctionWebSocket.auctionId === auctionId) {
        return window.auctionWebSocket;
    } else {
        const client = new AuctionWebSocketClient(auctionId, userId, options);
        window.auctionWebSocket = client;
        return client;
    }

}

document.addEventListener('DOMContentLoaded', function () {

    const auctionElement = document.querySelector('[data-auction-id]');
    const userElement = document.querySelector('[data-user-id]');

    if (auctionElement && userElement) {
        const auctionId = auctionElement.getAttribute('data-auction-id');
        const userId = userElement.getAttribute('data-user-id');

        if (auctionId && userId) {
            window.auctionWebSocket = initAuctionWebSocket(auctionId, userId);
        }
    }
});

// Clean up WebSocket
window.addEventListener('beforeunload', function () {
    if (window.auctionWebSocket) {
        window.auctionWebSocket.disconnect();
    }
});